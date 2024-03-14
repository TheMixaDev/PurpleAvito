package org.bigbrainmm.avitopricesapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.*;
import org.bigbrainmm.avitopricesapi.entity.DiscountBaseline;
import org.bigbrainmm.avitopricesapi.entity.SourceBaseline;
import org.bigbrainmm.avitopricesapi.repository.DiscountBaselineRepository;
import org.bigbrainmm.avitopricesapi.repository.SourceBaselineRepository;
import org.bigbrainmm.avitopricesapi.service.UpdateBaselineAndSegmentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "Получение цен!")
public class PriceController {

    private final SourceBaselineRepository sourceBaselineRepository;
    private final DiscountBaselineRepository discountBaselineRepository;
    private final UpdateBaselineAndSegmentsService updateBaselineAndSegmentsService;
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(PriceController.class);

    @Value("${USE_HASH}")
    private boolean useHash;

    @Schema(description = "Запрос цены")
    @PostMapping(value = "/price")
    public ResponseEntity<PriceResponse> getPrice(@RequestBody PriceRequest priceRequest) {
        // Описание алгоритма

        // заводим переменную price
        // есть ли пользователь в пользователях:
            // получаем сегменты
            // for segment in segments:
                // if not discount_matrix_exists: continue
                // price = findPrice(..., ..., ...)
                // if price != -1: break
        // if price != -1:
            // price = findPrice(base)
        // заполняем ответ

        boolean dbAvailable = updateBaselineAndSegmentsService.dataBaseIsAvailable();
        if (!isAvailable.get() || !dbAvailable) {
            if (!dbAvailable) updateBaselineAndSegmentsService.startTryingToConnectToDatabase();
            logger.info("Выполнен запрос на цену, но сервер недоступен, пытаюсь получить данные с другого СОЦ...");
            ResponseEntity<PriceResponse> responseEntity = tryGetPriceFromAnotherSOCs(priceRequest);
            if (!responseEntity.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                logger.info("Выполнен успешный редирект на другой СОЦ");
            }
            // Возват тела и статус кода из редиректа во избежании дублирования хэдеров
            return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
        }

        TreeNode microCategory = microCategoryRoot.getById(priceRequest.getMicroCategoryId());
        if (microCategory == null || microCategory.getName().equals("root")) {
            throw new InvalidDataException("Категория с id " + priceRequest.getMicroCategoryId() + " не найдена");
        }
        TreeNode location = locationsRoot.getById(priceRequest.getLocationId());
        if (location == null || location.getName().equals("root")) {
            throw new InvalidDataException("Локация с id " + priceRequest.getLocationId() + " не найдена");
        }

        PriceResponse price = null;
        if (userSegments.getUserSegments().containsKey(priceRequest.getUserId())) {
            List<Long> segments = userSegments.getUserSegments().get(priceRequest.getUserId());
            if (!segments.isEmpty()) {
                for (Long segment : segments) {
                    String matrixName = getMatrixNameBySegment(segment);
                    if (matrixName == null) continue;
                    price = findPrice(
                            microCategory,
                            location,
                            matrixName);
                    if (price != null) {
                        price.setUserSegmentId(segment);
                        break;
                    }
                }
            }
        }
        if (price == null) {
            price = findPrice(
                    microCategory,
                    location,
                    baselineMatrixAndSegments.getBaselineMatrix().getName()
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(price);
    }

    private PriceResponse findPrice(TreeNode microCategory, TreeNode location, String matrixName) {
        if (matrixIsCached(matrixName)) {
            logger.info("Ищем цену в кэше: " + matrixName);
            return findPriceInCache(microCategory, location, matrixName);
        } else {
            logger.info("Ищем цену в базе: " + matrixName + ", use hash: " + useHash);
            return findPriceRunTree(microCategory, microCategory, location, matrixName);
        }
    }

    private PriceResponse findPriceRunTree(TreeNode initialMicrocategory, TreeNode microCategory, TreeNode location, String matrixName) {
        // Описание алгоритма

        // select * from matrixName where location_id, mic_id
        // if price != NULL reutrn price, mic, location
        // else
            // берём верхнюю ноду категории
            // если она не null
                // return findPrice(initial_MID, parent_microcategory_id, LID)
            // берём верхнюю ноду локации
            // если она не нулл
                // return findPrice(initial_MID, initial_MID, parent_location_id)
        long id = useHash ? location.getId() + 4108 * (microCategory.getId() - 1) : -1;
        String sql = useHash ? "select * from " + matrixName + " where id = " + id + " and price is not null;"
                : "select * from " + matrixName + " where microcategory_id = " + microCategory.getId() + " and location_id = " + location.getId() + " and price is not null;";

        List<PriceResponse> res = jdbcTemplate.query(sql, (rs, rowNum) ->
                new PriceResponse(rs.getLong("price"), rs.getLong("location_id"), rs.getLong("microcategory_id"), matrixName, null));

        if (!res.isEmpty() && res.get(0).getPrice() != 0) return res.get(0);

        TreeNode parentNodeMic = microCategory.getParent();
        if (parentNodeMic != null) {
            return findPriceRunTree(initialMicrocategory, parentNodeMic, location, matrixName);
        }
        TreeNode parentNodeLoc = location.getParent();
        if (parentNodeLoc != null) {
            return findPriceRunTree(initialMicrocategory, initialMicrocategory, parentNodeLoc, matrixName);
        }
        return null;
    }

    private PriceResponse findPriceInCache(TreeNode microCategory, TreeNode location, String matrixName) {
        Map<String, Object> row = getRowByMicAndLoc(matrixName, microCategory.getId(), location.getId());
        if (row == null) return null;
        return new PriceResponse(
                wrOInt(row.get("found_price")),
                wrOInt(row.get("found_location_id")),
                wrOInt(row.get("found_microcategory_id")),
                matrixName,
                null
        );
    }

    private boolean matrixIsCached(String matrixName) {
        if (matrixName.contains("baseline_matrix")) {
            SourceBaseline baseline = sourceBaselineRepository.findByName(matrixName);
            if (baseline == null) return false;
            return baseline.getIsCached() != null && baseline.getIsCached();
        } else if (matrixName.contains("discount_matrix")) {
            DiscountBaseline discount = discountBaselineRepository.findByName(matrixName);
            if (discount == null) return false;
            return discount.getIsCached() != null && discount.getIsCached();
        }
        return false;
    }

    private Long wrOInt(Object o) {
        if (o == null) return null;
        if (o instanceof Long) return (Long) o;
        if (o instanceof Integer) return ((Integer) o).longValue();
        return Long.parseLong(o.toString());
    }

    private Map<String, Object> getRowByMicAndLoc(String name, long mic, long loc) {
        try {
            // Умнейшая формула вычисления хэшированного id
            long id = useHash ? loc + 4108L * (mic - 1) : -1;
            String sql = useHash ?
                    "select * from " + name + " where id = " + id + ";" :
                    "select * from " + name + " where microcategory_id = " + mic + " and location_id = " + loc + ";";
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping(value="/is_baseline_segments_updated", produces = "application/json")
    public ResponseEntity<Boolean> isBaselineMatrixUpdated(@RequestBody BaselineMatrixAndSegments bms) {
        return updateBaselineAndSegmentsService.isDataUpdated(bms) ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private String getMatrixNameBySegment(Long segmentId) {
        return baselineMatrixAndSegments.getDiscountSegments().stream().filter(
                x -> Objects.equals(x.getId(), segmentId)).findFirst().map(DiscountSegment::getName).orElse(null);
    }

    /**
     * В случае, если сервис не доступные (isAvailable == false), то пытаемся получить цену из других СОЦ
     * Своего рода проксирование. Сперва определяются доступные url СОЦ'ов, у которых isAvailable = true
     * Потом запрос перенаправляется к одному из этих СОЦ'ов, что вернул успешный ответ
     * Если все СОЦ'ы недоступны, то возвращаем статус 503 (но такое возможно только если прям ВСЕ СОЦы лежат)
     * @param priceRequest
     */
    private ResponseEntity<PriceResponse> tryGetPriceFromAnotherSOCs(PriceRequest priceRequest) {
        List<String> urls = Arrays.stream(updateBaselineAndSegmentsService.getServicesUrl().split(","))
                .filter(url -> !url.equals(updateBaselineAndSegmentsService.getSelfUrl())).toList();
        for (String url : urls) {
            ResponseEntity<String> response = null;
            boolean err;
            try {
                logger.info("Проверка статуса СОЦ: " + url);
                response = restTemplate.getForEntity(url + "/api/available/", String.class);
                err = false;
            } catch (Exception e) { err = true; }
            if (response != null && response.getStatusCode().equals(HttpStatus.OK) && !err) {
                String redirectUrl = url + "/price";
                ResponseEntity<PriceResponse> responseEntity = null;
                try {
                    logger.info("Статус OK, Запрос на СОЦ: " + redirectUrl);
                    responseEntity = restTemplate.postForEntity(redirectUrl, priceRequest, PriceResponse.class);
                } catch (HttpClientErrorException e) {
                    if (e.getRawStatusCode() == 400) {
                        ObjectMapper mapper = new ObjectMapper();
                        MessageResponse messageResponse = null;
                        try {
                            messageResponse = mapper.readValue(e.getResponseBodyAsString(), MessageResponse.class);
                            throw new InvalidDataException(messageResponse.getMessage());
                        } catch (JsonProcessingException jpe) {
                            throw new InvalidDataException("Message is not parsed");
                        }
                    }
                }
                if (responseEntity != null) return responseEntity;
            }
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse error(InvalidDataException ex) {
        return new MessageResponse(ex.getMessage());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public MessageResponse error(BadSqlGrammarException ex) {
        isAvailable.set(false);
        logger.info("Поменян статус сервера: " + isAvailable.get());
        updateBaselineAndSegmentsService.startUpdatingThread();
        return new MessageResponse(ex.getMessage());
    }


    public static class InvalidDataException extends RuntimeException {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}
