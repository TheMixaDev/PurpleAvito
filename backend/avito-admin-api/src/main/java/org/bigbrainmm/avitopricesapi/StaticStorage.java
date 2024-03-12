package org.bigbrainmm.avitopricesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.bigbrainmm.avitopricesapi.dto.TreeNode;
import org.bigbrainmm.avitopricesapi.dto.UserSegments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class StaticStorage {
    public static TreeNode microCategoryRoot;
    public static TreeNode locationsRoot;
    public static UserSegments userSegments;
    public static BaselineMatrixAndSegments baselineMatrixAndSegments;

    static {
        microCategoryRoot = TreeNode.buildTreeFromFile("microcategories.txt");
        locationsRoot = TreeNode.buildTreeFromFile("locations.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new ClassPathResource("json/user_segments.json").getInputStream());
            ObjectMapper mapper = new ObjectMapper();
            userSegments = mapper.readValue(isr, UserSegments.class);
            userSegments.getUserSegments().forEach((k, v) -> v.sort((x1, x2) -> Math.toIntExact(x2 - x1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            InputStreamReader isr = new InputStreamReader(new FileSystemResource("baseline_and_segments.json").getInputStream());
            ObjectMapper mapper = new ObjectMapper();
            StaticStorage.baselineMatrixAndSegments = mapper.readValue(isr, BaselineMatrixAndSegments.class);
            baselineMatrixAndSegments.setLastUpdate(System.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBaselineAndSegments(BaselineMatrixAndSegments bms) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Resource resource = new FileSystemResource("baseline_and_segments.json");
            File file = new File(String.valueOf(resource.getFile()));
            mapper.writeValue(file, bms);
            bms.setLastUpdate(System.currentTimeMillis());
            StaticStorage.baselineMatrixAndSegments = bms;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}