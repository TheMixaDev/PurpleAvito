package org.bigbrainmm.avitoadminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * DTO с пользователями и скидочными сегментами к ним
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Пользователи и скидочные сегменты к ним")
public class UserSegments {
    @Schema(description = "id пользователя -> список id скидочных сегментов, относящихся к пользователю", example = "[168, 290, 412]")
    private Map<Long, List<Long>> userSegments;
}
