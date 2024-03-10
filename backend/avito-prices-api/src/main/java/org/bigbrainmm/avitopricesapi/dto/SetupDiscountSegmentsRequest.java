package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Установить скидочные матрицы для соответствующих сегментов")
public class SetupDiscountSegmentsRequest {
    @Schema(description = "Лист из пар: идентификатор сегмента <-> имя скидочной матрицы для установки")
    private List<PairSegmentIdDiscountMatrix<Long, String>> discountSegments;
}
