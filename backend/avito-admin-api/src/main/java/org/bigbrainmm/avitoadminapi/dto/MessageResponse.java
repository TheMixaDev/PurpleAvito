package org.bigbrainmm.avitoadminapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Сообщение об ошибке
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сообщение об ошибке")
public class MessageResponse {
    @Schema(description = "Текст сообщения", example = "Любая информация в ответе запроса")
    private String message;
}
