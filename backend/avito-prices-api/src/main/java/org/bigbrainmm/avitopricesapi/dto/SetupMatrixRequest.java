package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Установить новую текущую матрицу по имени
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Установить новую текущую матрицу по имени")
public class SetupMatrixRequest {
    @Schema(description = "Имя матрицы", example = "baseline_matrix_1")
    @NotBlank(message = "Имя матрицы не может быть пустым")
    private String name;
}
