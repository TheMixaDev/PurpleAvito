package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bigbrainmm.avitopricesapi.model.TreeNode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Посмотреть дерево локаций")
public class GetLocationsTreeRequest {
    @Schema(description = "Дерево микрокатегорий")
    private TreeNode root;
}
