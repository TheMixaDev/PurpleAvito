package org.bigbrainmm.avitoadminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Координаты")
public class Coordinates {
    @Schema(description = "Широта", example = "55.751244")
    double lat;
    @Schema(description = "Долгота", example = "37.618423")
    double lon;
}
