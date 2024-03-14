package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Запрос цены
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос цены")
public class PriceRequest {

    @Schema(description = "id локации", example = "123")
    private Long locationId;
    @Schema(description = "id микрокатегории", example = "123")
    private Long microCategoryId;
    @Schema(description = "id пользователя", example = "123")
    private Long userId;
}
