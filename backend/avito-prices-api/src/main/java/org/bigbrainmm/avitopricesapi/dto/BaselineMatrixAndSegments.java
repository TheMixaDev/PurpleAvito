package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO: Текущая стандартная матрица и список скидочных сегментов
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Текущая стандартная матрица и список скидочных сегментов")
public class BaselineMatrixAndSegments {
    @Schema(description = "Стандартная матрица")
    Matrix baselineMatrix;
    @Schema(description = "Скидочные сегменты")
    List<DiscountSegment> discountSegments;
    @Schema(description = "Timestamp последнего обновления baseline или скидочных сегментов", example = "1751234123")
    Long lastUpdate;
}
