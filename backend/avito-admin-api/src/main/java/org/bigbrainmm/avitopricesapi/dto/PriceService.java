package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Сервис отдачи цен")
public class PriceService {
    @Schema(description = "URL сервиса", example = "http://localhost:8081")
    private String url;
    @Schema(description = "Статус соединения", example = "true")
    private boolean connected;
    @Schema(description = "Актуальность ценовых матриц", example = "true")
    private boolean actual;
    @Schema(description = "Время запроса к сервису", example = "5")
    private long ping;
    @Schema(description = "Имя сервиса", example = "Москва")
    private String name;
    @Schema(description = "Кооридаты")
    private Coordinates coordinates;
}
