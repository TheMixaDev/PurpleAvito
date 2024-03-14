package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Скидочный сегмент
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Скидочный сегмент")
public class DiscountSegment {
    @Schema(description = "Идентификатор сегмента", example = "1")
    @Id
    private Long id;
    @Schema(description = "Название скидочной матрицы, относящейся к сегменту", example = "discount_matrix_1")
    private String name;
}
