package org.bigbrainmm.avitopricesapi.service;

import io.netty.channel.ChannelOption;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;
import static org.bigbrainmm.avitopricesapi.StaticStorage.baselineMatrixAndSegments;

@RequiredArgsConstructor
@Service
public class SOCDelegatorService {
    @Value("${AVITO_PRICES_SERVICES_URLS}")
    private String servicesUrl;
    private final Logger logger = LoggerFactory.getLogger(SOCDelegatorService.class);
    @Getter
    private List<String> socUrls;

    static WebClient webClient = WebClient.create();

    @PostConstruct
    public void init() {
        socUrls = List.of(servicesUrl.split(","));
        sendCurrentBaselineAndSegmentsToSOCs();
    }

    public void sendCurrentBaselineAndSegmentsToSOCs() {
        String[] urls = socUrls.stream().map(url -> url + "/api/matrices/setup/baseline_segments").toArray(String[]::new);
        Arrays.stream(urls).forEach(url -> {
            webClient.post()
                    .uri(url)
                    .bodyValue(baselineMatrixAndSegments)
                    .retrieve()
                    .bodyToMono(BaselineMatrixAndSegments.class)
                    .onErrorResume(e -> Mono.empty())
                    .subscribe();
        });
    }

    public String isAllDelegatorsReadyMessage(BaselineMatrixAndSegments baselineMatrixAndSegments) {
        Tuple<Integer, Integer> result = isAllDelegatorsReady(baselineMatrixAndSegments);
        if(result.getFirst().intValue() != result.getSecond().intValue() || result.getSecond().intValue() == 0)
            return "Не все скидочные сервисы готовы к обновлению матрицы, готово " + result.getSecond() + " из " + result.getFirst();
        return null;
    }

    public Tuple<Integer, Integer> isAllDelegatorsReady(BaselineMatrixAndSegments baselineMatrixAndSegments) {
        ExecutorService executor = Executors.newFixedThreadPool(socUrls.size());

        List<CompletableFuture<Tuple<Boolean, Boolean>>> futures = socUrls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> checkPriceService(url, baselineMatrixAndSegments), executor))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        List<Tuple<Boolean, Boolean>> priceServices = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        int total = 0;
        int ready = 0;

        for(Tuple<Boolean, Boolean> priceService : priceServices) {
            if(priceService.getFirst()) {
                total++;
                if(priceService.getSecond())
                    ready++;
            }
        }

        logger.info("Total: " + total + ", ready: " + ready);
        
        return new Tuple<>(total, ready);
    }

    private Tuple<Boolean, Boolean> checkPriceService(String url, BaselineMatrixAndSegments baselineMatrixAndSegments) {
        boolean connected = true;
        boolean actual = false;
        try {
            WebClient.ResponseSpec responseSpec = webClient
                    .post()
                    .uri(url + "/is_baseline_segments_updated")
                    .bodyValue(baselineMatrixAndSegments)
                    .retrieve();
            HttpStatusCode statusCode = Objects.requireNonNull(responseSpec.toBodilessEntity()
                    .timeout(Duration.ofMillis(200)).block()).getStatusCode();
            actual = statusCode == HttpStatusCode.valueOf(200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() != HttpStatusCode.valueOf(404))
                connected = false;
        } catch (Exception e) {
            connected = false;
        }

        return new Tuple<>(connected, actual);
    }
}
