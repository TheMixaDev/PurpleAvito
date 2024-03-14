package org.bigbrainmm.avitoadminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bigbrainmm.avitoadminapi.entity.History;

import java.util.List;

/**
 * DTO: История изменений
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "История изменений")
public class HistoryResponse {

    @Schema(description = "История изменений")
    List<History> history;

    @Schema(description = "Общее количество изменений", example = "123")
    long total;
}
