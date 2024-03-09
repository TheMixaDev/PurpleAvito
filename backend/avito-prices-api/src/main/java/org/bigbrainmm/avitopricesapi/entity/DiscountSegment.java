package org.bigbrainmm.avitopricesapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Скидочный сегмент")
@Entity(name = "discount_segments")
public class DiscountSegment {
    @Schema(description = "Идентификатор сегмента", example = "1")
    @Id
    private Long id;
    @Schema(description = "Название скидочной матрицы, относящейся к сегменту", example = "discount_matrix_1")
    @Column(name = "name")
    private String name;
}
