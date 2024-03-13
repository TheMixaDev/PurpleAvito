package org.bigbrainmm.avitoadminapi.dto;

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
@Schema(description = "Список всех матриц")
public class AllMatrixResponse {
    @Schema(description = "Стандартные матрицы")
    List<Matrix> baselineMatrices;
    @Schema(description = "Скидочные матрицы")
    List<Matrix> discountMatrices;
}
