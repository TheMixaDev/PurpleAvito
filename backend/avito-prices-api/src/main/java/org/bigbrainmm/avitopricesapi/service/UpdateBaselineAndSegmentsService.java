package org.bigbrainmm.avitopricesapi.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.bigbrainmm.avitopricesapi.dto.DiscountSegment;
import org.bigbrainmm.avitopricesapi.dto.Matrix;
import org.bigbrainmm.avitopricesapi.repository.DiscountBaselineRepository;
import org.bigbrainmm.avitopricesapi.repository.SourceBaselineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;

/**
 * Сервис обновления, актуализации, проверки и других методов для того, чтобы
 * минимизировать моргание цен. Подробнее о нюансах работы будет в презентации.
 */
@Service
@RequiredArgsConstructor
@Getter
public class UpdateBaselineAndSegmentsService {
    @Value("${AVITO_ADMIN_API_URL}")
    private String adminServerUrl;
    @Value("${SELF_URL}")
    private String selfUrl;
    @Value("${AVITO_PRICES_SERVICES_URLS}")
    private String servicesUrl;
    @Value("${MAX_DATABASE_PING_IN_MILLIS}")
    private int maxDatabasePingInMillis;

    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final DiscountBaselineRepository discountBaselineRepository;
    private final SourceBaselineRepository sourceBaselineRepository;
    private final Logger logger = LoggerFactory.getLogger(UpdateBaselineAndSegmentsService.class);
    private String url;

    /**
     * Загрузка основной ценовой матрицы и скидочных сегментов при старте сервера
     * Запрашивает данные с сервера и проверяет их актуальность в своей бд, если всё ок isAvailable = true
     * Если нет, то запускается поток обновления запросами с основного сервера 5 раз с timestamp в 1000 миллисекунд
     * Если ответ от него так и не поступил, тогда СОЦ берёт URL других СОЦ'ов
     * Получает данные с них и присваивает себе те, время обновления которых наиболее актуальное
     */
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


    private boolean startUpdatingThreadStarted = false;

    /**
     * Запуск потока обновления.
     * Данные запрашиваются с основного сервера 5 раз с timestamp в 1000 миллисекунд
     * Если ответ от него так и не поступил, тогда СОЦ берёт URL других СОЦ'ов
     * Получает данные с них и присваивает себе те, время обновления которых наиболее актуальное
     */
    public void startUpdatingThread() {
        if (startUpdatingThreadStarted) return;
        Thread thread = new Thread(() -> {
            int counter = 0, maxAttempts = 5;
            boolean success = updateBaselineAndSegmentsFromServer();
            while (!isDataUpdated(baselineMatrixAndSegments) || !success) {
                try {
                    Thread.sleep(1000);
                    counter++;
                    success = updateBaselineAndSegmentsFromServer();
                    if (counter >= maxAttempts) {
                        tryToUpdateBaselineAndSegmentsFromAnotherSOCs();
                        break;
                    }
                } catch (InterruptedException ignored) { }
            }
            if (counter < maxAttempts) {
                isAvailable.set(true);
                logger.info("Поменян статус сервера: " + isAvailable.get());
            }
            startUpdatingThreadStarted = false;
        });
        thread.start();
        startUpdatingThreadStarted = true;
    }

    private boolean tryingToConnectToDatabaseStarted = false;

    /**
     * Поток попытки восстановить соединение с базой данных
     */
    public void startTryingToConnectToDatabase() {
        if (tryingToConnectToDatabaseStarted) return;
        Thread thread = new Thread(() -> {
            while (!dataBaseIsAvailable()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) { }
            }
            logger.info("Соединение с базой данных восстановлено");
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
            tryingToConnectToDatabaseStarted = false;
        });
        thread.start();
        tryingToConnectToDatabaseStarted = true;
    }

    /**
     * Update baseline and segments from server boolean.
     *
     * @return the boolean
     */
    public boolean updateBaselineAndSegmentsFromServer() {
        try {
            baselineMatrixAndSegments = restTemplate.getForEntity(url, BaselineMatrixAndSegments.class).getBody();
            assert baselineMatrixAndSegments != null;
            baselineMatrixAndSegments.setLastUpdate(System.currentTimeMillis());
            StaticStorage.saveBaselineAndSegments(baselineMatrixAndSegments);
            logger.info("baselineMatrixAndSegments обновлён");
            return true;
        } catch (Exception e) {
            logger.error("Не удалось обновить baselineMatrixAndSegments: " + e.getMessage());
            return false;
        }
    }

    /**
     * Попытка получения данных с других СОЦ'ов
     * Получает данные с них и присваивает себе те, время обновления которых наиболее актуальное
     * ЕСЛИ получилось так, что все СОЦ'ы лежат, то он берёт весь удар на себя
     */
    public void tryToUpdateBaselineAndSegmentsFromAnotherSOCs() {
        try {
            List<BaselineMatrixAndSegments> bs = new ArrayList<>();
            Arrays.stream(servicesUrl.split(",")).filter(s -> !s.isEmpty() && !s.equals(selfUrl)).forEach(s -> {
                try {
                    BaselineMatrixAndSegments b = restTemplate.getForEntity(s + "/api/matrices/setup", BaselineMatrixAndSegments.class).getBody();
                    bs.add(b);
                } catch (Exception e) {
                    logger.error("Не удалось получить baselineMatrixAndSegments c СОЦ: " + s + " " + e.getMessage());
                }
            });
            long maxLastUpdate = -1;
            for (BaselineMatrixAndSegments b : bs) {
                if (b.getLastUpdate() > maxLastUpdate) {
                    maxLastUpdate = b.getLastUpdate();
                    baselineMatrixAndSegments = b;
                }
            }
            if (maxLastUpdate == -1) {
                throw new Exception();
            }
            logger.info("baselineMatrixAndSegments обновлён с другого СОЦа c lastUpdate: " + maxLastUpdate);
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
        } catch (Exception e) {
            logger.error("Не удалось обновить baselineMatrixAndSegments из других СОЦ'ов: " + e.getMessage());
            logger.info("Теперь я супермен, все упали и я единственный отвечаю за отдачу цен");
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
        }
    }

    /**
     * Есть ли в базе данных, прикреплённой к сервису текущая основная ценовая матрица и скидочные сегменты
     *
     * @param t the t
     * @return the boolean
     */
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

    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Проверяет доступность базы данных с максимальным пингом в MAX_DATABASE_PING_IN_MILLIS миллисекунд
     * Максимальный пинг указывается в константе MAX_DATABASE_PING_IN_MILLIS переменной окружения
     *
     * @return the boolean
     */
    public boolean dataBaseIsAvailable() {
        try {
            Future<?> future = executor.submit(() -> {
                jdbcTemplate.queryForObject("select 1", Integer.class);
            });
            future.get(maxDatabasePingInMillis, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            isAvailable.set(false);
            logger.info("Поменян статус сервера: " + isAvailable.get());
            logger.error("Разорвано соединение с базой данных...");
            return false;
        }
    }
}
