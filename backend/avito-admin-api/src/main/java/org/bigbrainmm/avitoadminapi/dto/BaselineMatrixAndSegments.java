package org.bigbrainmm.avitoadminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

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


