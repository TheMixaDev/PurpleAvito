package org.bigbrainmm.avitopricesapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Список всех сервисов отдачи цен")
public class AllPriceServicesResponse {
    @Schema(description = "Сервисы отдачи цен")
    List<PriceService> priceServices;
}
