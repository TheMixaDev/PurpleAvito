package org.bigbrainmm.avitopricesapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SaveBaselineAndSegmentsService {
    public void saveDataToStorage(BaselineMatrixAndSegments baselineMatrixAndSegments) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Resource resource = new FileSystemResource("baseline_and_segments.json");
            File file = new File(String.valueOf(resource.getFile()));
            mapper.writeValue(file, baselineMatrixAndSegments);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
