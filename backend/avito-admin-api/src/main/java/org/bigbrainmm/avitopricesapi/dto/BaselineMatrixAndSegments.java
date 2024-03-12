package org.bigbrainmm.avitopricesapi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Текущая стандартная матрица и список скидочных сегментов")
public class BaselineMatrixAndSegments {
    @Schema(description = "Стандартная матрица")
    Matrix baselineMatrix;
    @Schema(description = "Скидочные сегменты")
    List<DiscountSegment> discountSegments;
    @Schema(description = "Timestamp последнего обновления baseline или скидочных сегментов", example = "1751234123")
    Long lastUpdate;
}


