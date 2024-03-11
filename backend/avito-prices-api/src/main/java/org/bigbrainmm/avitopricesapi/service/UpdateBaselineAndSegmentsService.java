package org.bigbrainmm.avitopricesapi.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.bigbrainmm.avitopricesapi.dto.DiscountSegment;
import org.bigbrainmm.avitopricesapi.dto.Matrix;
import org.bigbrainmm.avitopricesapi.repository.DiscountBaselineRepository;
import org.bigbrainmm.avitopricesapi.repository.SourceBaselineRepository;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;
import java.util.List;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;

@Service
@RequiredArgsConstructor
public class UpdateBaselineAndSegmentsService {
    @Value("${AVITO_ADMIN_API_URL}")
    private String adminServerUrl;

    private final RestTemplate restTemplate;
    private final DiscountBaselineRepository discountBaselineRepository;
    private final SourceBaselineRepository sourceBaselineRepository;
    private final Logger logger = LoggerFactory.getLogger(UpdateBaselineAndSegmentsService.class);
    private String url;

    @PostConstruct
    public void loadBaselineAndSegments() {
        url = adminServerUrl + "/api/matrices/setup";
        isAvailable.set(false);
        logger.info("Поменян статус сервера: " + isAvailable.get());
        logger.info("Обновление baseline и discount segments");
        boolean success = updateBaselineAndSegmentsFromServer();
        if (success && isDataUpdated(baselineMatrixAndSegments)) {
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
        } else {
            logger.info("baselineMatrixAndSegments не обновлён");
            logger.info("Запуск потока обновления baseline и discount segments");
            startUpdatingThread();
        }
    }

    public void startUpdatingThread() {
        Thread thread = new Thread(() -> {
            boolean success = updateBaselineAndSegmentsFromServer();;
            while (!isDataUpdated(baselineMatrixAndSegments) || !success) {
                try {
                    Thread.sleep(1000);
                    success = updateBaselineAndSegmentsFromServer();
                } catch (InterruptedException ignored) { }
            }
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
        });
        thread.start();
    }

    public boolean updateBaselineAndSegmentsFromServer() {
        try {
            baselineMatrixAndSegments = restTemplate.getForEntity(url, BaselineMatrixAndSegments.class).getBody();
            lastUpdate = System.currentTimeMillis();
            StaticStorage.saveBaselineAndSegments(baselineMatrixAndSegments);
            logger.info("baselineMatrixAndSegments обновлён");
            return true;
        } catch (Exception e) {
            logger.error("Не удалось обновить baselineMatrixAndSegments: " + e.getMessage());
            return false;
        }
    }

    public boolean isDataUpdated(BaselineMatrixAndSegments t) {
        return hasBaselineMatrix(t.getBaselineMatrix()) && hasDiscountSegments(t.getDiscountSegments());
    }

    private boolean hasBaselineMatrix(Matrix matrix) {
        return sourceBaselineRepository.findByName(matrix.getName()) != null;
    }

    private boolean hasDiscountSegments(List<DiscountSegment> discountSegments) {
        for (DiscountSegment segment : discountSegments) {
            String discountMatrixName = segment.getName();
            if (discountMatrixName == null || discountMatrixName.isEmpty() || discountMatrixName.equals("null")) continue;
            if (discountBaselineRepository.findByName(discountMatrixName) == null) {
                return false;
            }
        }
        return true;
    }
}
