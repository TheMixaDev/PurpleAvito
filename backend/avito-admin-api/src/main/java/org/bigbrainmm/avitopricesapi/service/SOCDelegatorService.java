package org.bigbrainmm.avitopricesapi.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;

@RequiredArgsConstructor
@Service
public class SOCDelegatorService {
    @Value("${AVITO_PRICES_SERVICES_URLS}")
    private String servicesUrl;
    private final Logger logger = LoggerFactory.getLogger(SOCDelegatorService.class);
    @Getter
    private List<String> socUrls;

    @PostConstruct
    public void init() {
        socUrls = List.of(servicesUrl.split(","));
        sendCurrentBaselineAndSegmentsToSOCs();
    }

    public void sendCurrentBaselineAndSegmentsToSOCs() {
        WebClient webClient = WebClient.create();
        String[] urls = socUrls.stream().map(url -> url + "/api/matrices/setup/baseline_segments").toArray(String[]::new);

        Flux.fromArray(urls)
                .flatMap(url ->
                        webClient.post()
                        .uri(url)
                        .body(BodyInserters.fromValue(baselineMatrixAndSegments))
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(error -> Mono.empty())
                        .doOnNext(response -> logger.info("Response from " + url + ": " + response))
                ).subscribe();
    }
}
