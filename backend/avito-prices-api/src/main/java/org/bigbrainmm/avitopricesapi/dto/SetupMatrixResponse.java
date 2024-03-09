package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bigbrainmm.avitopricesapi.entity.DiscountSegment;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Текущая стандартная матрица и список скидочных сегментов")
public class SetupMatrixResponse {
    @Schema(description = "Стандартная матрица")
    Matrix baselineMatrix;
    @Schema(description = "Скидочные сегменты")
    List<DiscountSegment> discountSegments;
}
