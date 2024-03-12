package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Содержимое матрицы")
public class MatrixContent {
    @Schema(description = "Строки матрицы под фильтрами")
    List<Map<String, Object>> rows;
    @Schema(description = "Общее количество строк матрицы", example = "123")
    Integer total;
}
