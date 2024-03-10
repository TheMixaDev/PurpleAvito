package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.bigbrainmm.avitopricesapi.StaticStorage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price")
@Tag(name = "Получение цен!")
public class PriceController {

    private final JdbcTemplate jdbcTemplate;

    @Schema(description = "Запрос цены")
    @PostMapping(value = "/")
    public PriceResponse getPrice(@RequestBody PriceRequest priceRequest) {
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

        if (microCategoryRoot.getById(priceRequest.getMicroCategoryId()) == null) {
            throw new InvalidDataException("Категория с id " + priceRequest.getMicroCategoryId() + " не найдена");
        }
        if (locationsRoot.getById(priceRequest.getLocationId()) == null) {
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
                            priceRequest.getMicroCategoryId(),
                            priceRequest.getMicroCategoryId(),
                            priceRequest.getLocationId(),
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
                    priceRequest.getMicroCategoryId(),
                    priceRequest.getMicroCategoryId(),
                    priceRequest.getLocationId(),
                    baselineMatrixAndSegments.getBaselineMatrix().getName()
            );
        }
        return price;
    }

    private PriceResponse findPrice(long initialMicroCategoryId, long microCategoryId, long locationId, String tableName) {
        // select * from tableName where location_id, mic_id
        // if price != NULL reutrn price, mic, location
        // else
            // берём верхнюю ноду категории
            // если она не null
                // return findPrice(initial_MID, parent_microcategory_id, LID)
            // берём верхнюю ноду локации
            // если она не нулл
                // return findPrice(initial_MID, initial_MID, parent_location_id)
        String sql = "select * from " + tableName + " where microcategory_id = " + microCategoryId + " and location_id = " + locationId + ";";
        List<PriceResponse> res = jdbcTemplate.query(sql, (rs, rowNum) ->
                new PriceResponse(rs.getLong("price"), rs.getLong("location_id"), rs.getLong("microcategory_id"), tableName, null));
        if (!res.isEmpty()) return res.get(0);

        TreeNode parentNodeMic = microCategoryRoot.getById(microCategoryId).getParent();
        if (parentNodeMic != null) {
            return findPrice(initialMicroCategoryId, parentNodeMic.getId(), locationId, tableName);
        }
        TreeNode parentNodeLoc = locationsRoot.getById(locationId).getParent();
        if (parentNodeLoc != null) {
            return findPrice(initialMicroCategoryId, initialMicroCategoryId, parentNodeLoc.getId(), tableName);
        }
        return null;
    }

    private String getMatrixNameBySegment(Long segmentId) {
        return baselineMatrixAndSegments.getDiscountSegments().stream().filter(
                x -> Objects.equals(x.getId(), segmentId)).findFirst().map(DiscountSegment::getName).orElse(null);
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse error(InvalidDataException ex) {
        return new ErrorResponse(ex.getMessage());
    }


    public static class InvalidDataException extends RuntimeException {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}
