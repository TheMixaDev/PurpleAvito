package org.bigbrainmm.avitoadminapi.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitoadminapi.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bigbrainmm.avitoadminapi.StaticStorage.baselineMatrixAndSegments;

/**
 * Делегатор севисов отдачи цен, невероятно крутая штука
 * Пояснение: СОЦ - сервис отдачи цен
 */
@RequiredArgsConstructor
@Service
public class SOCDelegatorService {

    /**
     * URL адреса СОЦов
     * Подгружается из переменной окружения
     */
    @Value("${AVITO_PRICES_SERVICES_URLS}")
    private String servicesUrl;
    private final Logger logger = LoggerFactory.getLogger(SOCDelegatorService.class);
    @Getter
    private List<String> socUrls;

    /**
     * Веб клиент для отправки запросов на другие СОЦы
     */
    static WebClient webClient = WebClient.create();

    /**
     * Инициализация при старте сервера
     * + Отправка данных о текущей основной матрице и сегментах на другие СОЦы при старте
     */
    @PostConstruct
    public void init() {
        socUrls = List.of(servicesUrl.split(","));
        sendCurrentBaselineAndSegmentsToSOCs();
    }

    /**
     * Отправка данных о текущей основной матрице и сегментах на другие СОЦы
     * Выполняется асинхронно, ответ не ожидается, необходимо исключительно для оповещения других соцов
     * о замене текущей матрицы и сегментов
     */
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

    /**
     * Проверяет, что все активные сервисы готовы к изменению главной матрицы
     *
     * @param baselineMatrixAndSegments the baseline matrix and segments
     * @return the string
     */
    public String isAllDelegatorsReadyMessage(BaselineMatrixAndSegments baselineMatrixAndSegments) {
        Tuple<Integer, Integer> result = isAllDelegatorsReady(baselineMatrixAndSegments);
        if(result.getFirst().intValue() != result.getSecond().intValue() || result.getSecond().intValue() == 0)
            return "Не все скидочные сервисы готовы к обновлению матрицы, готово " + result.getSecond() + " из " + result.getFirst();
        return null;
    }

    private Tuple<Integer, Integer> isAllDelegatorsReady(BaselineMatrixAndSegments baselineMatrixAndSegments) {
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
