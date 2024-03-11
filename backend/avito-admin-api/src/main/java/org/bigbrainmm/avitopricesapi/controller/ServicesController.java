package org.bigbrainmm.avitopricesapi.controller;

import io.netty.channel.ChannelOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.AllPriceServicesResponse;
import org.bigbrainmm.avitopricesapi.dto.PriceService;
import org.bigbrainmm.avitopricesapi.service.SOCDelegatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/services")
@RestController
@Tag(name = "Работа с сервисами отдачи цен")
public class ServicesController {
    @Value("${AVITO_PRICES_API_STATUS_TIMEOUT}")
    private static int statusTimeout;
    private final SOCDelegatorService socDelegatorService;
    static WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                            HttpClient.create()
                                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, statusTimeout)
                    )
            )
            .build();
    @GetMapping(produces = "application/json")
    @Operation(summary = "Посмотреть статус сервисов отдачи цен")
    public AllPriceServicesResponse getServices() {
        List<String> urls = socDelegatorService.getSocUrls();
        ExecutorService executor = Executors.newFixedThreadPool(urls.size());

        List<CompletableFuture<PriceService>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> checkPriceService(url), executor))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        List<PriceService> priceServices = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return new AllPriceServicesResponse(priceServices);
    }

    private static PriceService checkPriceService(String url) {
        boolean connected = true;
        boolean actual = false;
        long ping = 999;

        try {
            long start = System.currentTimeMillis();
            WebClient.ResponseSpec responseSpec = webClient
                    .get()
                    .uri(url + "/api/available/")
                    .retrieve();
            ping = System.currentTimeMillis() - start;
            HttpStatusCode statusCode = Objects.requireNonNull(responseSpec.toBodilessEntity().block()).getStatusCode();
            actual = statusCode == HttpStatusCode.valueOf(200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() != HttpStatusCode.valueOf(400))
                connected = false;
        } catch (Exception e) {
            connected = false;
        }

        return new PriceService(url, connected, actual, ping);
    }
}