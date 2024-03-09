package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Установить новую сегменту новую матрицу по id сегмента и name матрицы")
public class SetupDiscountSegmentRequest {
    @Schema(description = "Идентификатор сегмента", example = "1")
    @NotBlank(message = "Идентификатор сегмента не может быть пустым")
    private Long id;
    @Schema(description = "Имя матрицы", example = "Матрица 1")
    @NotBlank(message = "Имя матрицы не может быть пустым")
    private String name;
}
