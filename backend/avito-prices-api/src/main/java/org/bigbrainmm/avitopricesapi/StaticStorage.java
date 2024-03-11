package org.bigbrainmm.avitopricesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.bigbrainmm.avitopricesapi.dto.TreeNode;
import org.bigbrainmm.avitopricesapi.dto.UserSegments;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;

public class StaticStorage {
    public static TreeNode microCategoryRoot;
    public static TreeNode locationsRoot;
    public static BaselineMatrixAndSegments baselineMatrixAndSegments;
    public static UserSegments userSegments;

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
            InputStreamReader isr = new InputStreamReader(new ClassPathResource("json/current_baseline.json").getInputStream());
            ObjectMapper mapper = new ObjectMapper();
            StaticStorage.baselineMatrixAndSegments = mapper.readValue(isr, BaselineMatrixAndSegments.class);
            // ТУДУ: ОБРАЩЕНИЕ К СЕРВЕРУ ЗА ОБНОВЛЕНИЕМ ДАННЫХ
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
