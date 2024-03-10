package org.bigbrainmm.avitopricesapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.UserSegments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class LoadUserSegments {
    @PostConstruct
    public void load() {
        try {
            Resource resource = new ClassPathResource("json/user_segments.json");
            File file = resource.getFile();
            ObjectMapper mapper = new ObjectMapper();
            StaticStorage.userSegments = mapper.readValue(file, UserSegments.class);
            StaticStorage.userSegments.getUserSegments().forEach((k, v) -> v.sort((x1, x2) -> Math.toIntExact(x2 - x1)));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
