package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Пара значений для установки скидочной матрицы в скидочный сегмент")
public class PairSegmentIdDiscountMatrix<K, V> {
    private K segmentId;
    private V discountMatrixName;
}
