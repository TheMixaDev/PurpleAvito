package org.bigbrainmm.avitopricesapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class LoadBaselineAndSegments {
    @PostConstruct
    public void load() {
        try {
            Resource resource = new ClassPathResource("json/current_baseline.json");
            File file = resource.getFile();
            ObjectMapper mapper = new ObjectMapper();
            StaticStorage.baselineMatrixAndSegments = mapper.readValue(file, BaselineMatrixAndSegments.class);
            // ТУДУ: ОБРАЩЕНИЕ К СЕРВЕРУ ЗА ОБНОВЛЕНИЕМ ДАННЫХ
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
