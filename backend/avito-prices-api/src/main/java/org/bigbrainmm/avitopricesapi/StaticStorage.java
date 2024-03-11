package org.bigbrainmm.avitopricesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.bigbrainmm.avitopricesapi.dto.TreeNode;
import org.bigbrainmm.avitopricesapi.dto.UserSegments;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class StaticStorage {
    public static TreeNode microCategoryRoot;
    public static TreeNode locationsRoot;
    public static BaselineMatrixAndSegments baselineMatrixAndSegments;
    public static UserSegments userSegments;

    static {
        microCategoryRoot = TreeNode.buildTreeFromFile("microcategories.txt");
        locationsRoot = TreeNode.buildTreeFromFile("locations.txt");
        try {
            Resource resource = new ClassPathResource("json/user_segments.json");
            File file = resource.getFile();
            ObjectMapper mapper = new ObjectMapper();
            userSegments = mapper.readValue(file, UserSegments.class);
            userSegments.getUserSegments().forEach((k, v) -> v.sort((x1, x2) -> Math.toIntExact(x2 - x1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Resource resource = new ClassPathResource("json/current_baseline.json");
            File file = resource.getFile();
            ObjectMapper mapper = new ObjectMapper();
            StaticStorage.baselineMatrixAndSegments = mapper.readValue(file, BaselineMatrixAndSegments.class);
            // ТУДУ: ОБРАЩЕНИЕ К СЕРВЕРУ ЗА ОБНОВЛЕНИЕМ ДАННЫХ
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
