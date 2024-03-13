package org.bigbrainmm.avitopricesapi.controller;

import io.netty.channel.ChannelOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.AllPriceServicesResponse;
import org.bigbrainmm.avitopricesapi.dto.Coordinates;
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

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@RequestMapping("/api/services")
@RestController
@Tag(name = "Работа с сервисами отдачи цен", description = "Посмотреть статус сервисов отдачи цен в форматах, описанных к ручкам")
public class ServicesController {
    @Value("${AVITO_PRICES_API_STATUS_TIMEOUT}")
    private static int statusTimeout;
    private final SOCDelegatorService socDelegatorService;
    @Value("${AVITO_PRICES_SERVICES_NAMES}")
    private String names;
    @Value("${AVITO_PRICES_SERVICES_COORDS}")
    private String coords;
    static WebClient webClient = WebClient.create();
    @GetMapping(produces = "application/json")
    @Operation(summary = "Посмотреть статус сервисов отдачи цен")
    public AllPriceServicesResponse getServices() {
        List<String> urls = socDelegatorService.getSocUrls();
        List<Integer> indexes = IntStream.rangeClosed(0, urls.size() - 1)
                .boxed()
                .toList();
        ExecutorService executor = Executors.newFixedThreadPool(urls.size());

        List<CompletableFuture<PriceService>> futures = indexes.stream()
                .map(index -> CompletableFuture.supplyAsync(() -> checkPriceService(index), executor))
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

    private PriceService checkPriceService(int index) {
        List<String> urls = socDelegatorService.getSocUrls();
        boolean connected = true;
        boolean actual = false;
        long ping = 999;
        String url = urls.get(index);
        String name = names.split(",")[index];
        String coordsString = coords.split("\\|")[index];
        Coordinates coordinates = new Coordinates(
            Double.parseDouble(coordsString.split(",")[0]),
            Double.parseDouble(coordsString.split(",")[1])
        );
        try {
            long start = System.currentTimeMillis();
            WebClient.ResponseSpec responseSpec = webClient
                    .get()
                    .uri(url + "/api/available/")
                    .retrieve();
            ping = System.currentTimeMillis() - start;
            HttpStatusCode statusCode = Objects.requireNonNull(responseSpec.toBodilessEntity()
                    .timeout(Duration.ofMillis(200)).block()).getStatusCode();
            actual = statusCode == HttpStatusCode.valueOf(200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() != HttpStatusCode.valueOf(503))
                connected = false;
        } catch (Exception e) {
            connected = false;
        }

        return new PriceService(url, connected, actual, ping, name, coordinates);
    }
}
