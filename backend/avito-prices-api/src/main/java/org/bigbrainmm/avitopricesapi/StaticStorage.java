package org.bigbrainmm.avitopricesapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bigbrainmm.avitopricesapi.dto.BaselineMatrixAndSegments;
import org.bigbrainmm.avitopricesapi.dto.TreeNode;
import org.bigbrainmm.avitopricesapi.dto.UserSegments;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Статичное хранилище
 */
@Service
public class StaticStorage {
    /**
     * Флаг доступности сервера
     * Невероятно ключевой флаг в нашей реализации, работает для синхронизации в данных между
     * сервисами отдачи цен. Подробнее будет рассказано в презентации
     */
    public static AtomicBoolean isAvailable = new AtomicBoolean(false);
    /**
     * Дерево микрокатегорий
     */
    public static TreeNode microCategoryRoot;
    /**
     * Дерево локаций
     */
    public static TreeNode locationsRoot;
    /**
     * Текущая основная матрица и сегменты со скидочными матрицами, которые им принадлежат
     */
    public static BaselineMatrixAndSegments baselineMatrixAndSegments;
    /**
     * Id пользователей и их скидочных сегменты
     */
    public static UserSegments userSegments;

    // Инициализация
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Сохраняет текущую ценовую матрицу и скидочные матрицы в локальное хранилище
     *
     * @param bms the Baseline Matrix And Segments
     */
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
