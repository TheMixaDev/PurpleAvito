package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Ответ на запрос цены
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на запрос цены")
public class PriceResponse {
    @Schema(description = "цена", example = "123")
    private Long price;
    @Schema(description = "id локации", example = "123")
    private Long locationId;
    @Schema(description = "id микрокатегории", example = "123")
    private Long microCategoryId;
    @Schema(description = "Название матрицы", example = "discount_matrix_id")
    private String matrixName;
    @Schema(description = "id скидочного сегмента", example = "290")
    private Long userSegmentId;
}
