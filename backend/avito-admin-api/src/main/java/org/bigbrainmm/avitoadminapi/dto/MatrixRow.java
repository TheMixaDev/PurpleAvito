package org.bigbrainmm.avitoadminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Строка в матрице
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Строка в матрице")
public class MatrixRow {
    @Schema(description = "цена", example = "123")
    private Long price;
    @Schema(description = "id локации", example = "123")
    private Long locationId;
    @Schema(description = "id микрокатегории", example = "123")
    private Long microCategoryId;
    @Schema(description = "найденная цена", example = "123")
    private Long foundPrice;
    @Schema(description = "найденный id локации", example = "123")
    private Long foundLocationId;
    @Schema(description = "найденный id микрокатегории", example = "123")
    private Long foundMicroCategoryId;
    @Schema(description = "Название матрицы", example = "discount_matrix_id")
    private String matrixName;
    @Schema(description = "id скидочного сегмента", example = "290")
    private Long userSegmentId;
}