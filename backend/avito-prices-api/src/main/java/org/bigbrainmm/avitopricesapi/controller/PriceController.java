package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.*;
import org.bigbrainmm.avitopricesapi.service.UpdateBaselineAndSegmentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "Получение цен!")
public class PriceController {

    private final UpdateBaselineAndSegmentsService updateBaselineAndSegmentsService;
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(PriceController.class);

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

        if (!isAvailable.get()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if (!updateBaselineAndSegmentsService.dataBaseIsAvailable()) {
            isAvailable.set(false);
            logger.info("Поменян статус сервера: " + isAvailable.get());
            updateBaselineAndSegmentsService.startTryingToConnectToDatabase();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        TreeNode microCategory = microCategoryRoot.getById(priceRequest.getMicroCategoryId());
        if (microCategory == null) {
            throw new InvalidDataException("Категория с id " + priceRequest.getMicroCategoryId() + " не найдена");
        }
        TreeNode location = locationsRoot.getById(priceRequest.getLocationId());
        if (location == null) {
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
                    microCategory,
                    location,
                    baselineMatrixAndSegments.getBaselineMatrix().getName()
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(price);
    }

    private PriceResponse findPrice(TreeNode initialMicrocategory, TreeNode microCategory, TreeNode location, String tableName) {
        // Описание алгоритма

        // select * from tableName where location_id, mic_id
        // if price != NULL reutrn price, mic, location
        // else
            // берём верхнюю ноду категории
            // если она не null
                // return findPrice(initial_MID, parent_microcategory_id, LID)
            // берём верхнюю ноду локации
            // если она не нулл
                // return findPrice(initial_MID, initial_MID, parent_location_id)

        String sql = "select * from " + tableName + " where microcategory_id = " + microCategory.getId() + " and location_id = " + location.getId() + " and price is not null;";
        List<PriceResponse> res = jdbcTemplate.query(sql, (rs, rowNum) ->
                new PriceResponse(rs.getLong("price"), rs.getLong("location_id"), rs.getLong("microcategory_id"), tableName, null));
        if (!res.isEmpty() && res.get(0).getPrice() != 0) return res.get(0);

        TreeNode parentNodeMic = microCategory.getParent();
        if (parentNodeMic != null) {
            return findPrice(initialMicrocategory, parentNodeMic, location, tableName);
        }
        TreeNode parentNodeLoc = location.getParent();
        if (parentNodeLoc != null) {
            return findPrice(initialMicrocategory, initialMicrocategory, parentNodeLoc, tableName);
        }
        return null;
    }

    @PostMapping(value="/is_baseline_segments_updated", produces = "application/json")
    public ResponseEntity<Boolean> isBaselineMatrixUpdated(@RequestBody BaselineMatrixAndSegments bms) {
        return updateBaselineAndSegmentsService.isDataUpdated(bms) ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private String getMatrixNameBySegment(Long segmentId) {
        return baselineMatrixAndSegments.getDiscountSegments().stream().filter(
                x -> Objects.equals(x.getId(), segmentId)).findFirst().map(DiscountSegment::getName).orElse(null);
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse error(InvalidDataException ex) {
        return new MessageResponse(ex.getMessage());
    }


    public static class InvalidDataException extends RuntimeException {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}
