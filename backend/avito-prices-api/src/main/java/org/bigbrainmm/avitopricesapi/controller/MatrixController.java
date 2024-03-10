package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.*;
import org.bigbrainmm.avitopricesapi.entity.CurrentBaselineMatrix;
import org.bigbrainmm.avitopricesapi.entity.DiscountBaseline;
import org.bigbrainmm.avitopricesapi.entity.DiscountSegment;
import org.bigbrainmm.avitopricesapi.entity.SourceBaseline;
import org.bigbrainmm.avitopricesapi.repository.CurrentBaselineMatrixRepository;
import org.bigbrainmm.avitopricesapi.repository.DiscountBaselineRepository;
import org.bigbrainmm.avitopricesapi.repository.DiscountSegmentsRepository;
import org.bigbrainmm.avitopricesapi.repository.SourceBaselineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/matrices")
@RestController
@Tag(name = "Работа с матрицами")
public class MatrixController {

    private final DiscountBaselineRepository discountBaselineRepository;
    private final SourceBaselineRepository sourceBaselineRepository;
    private final CurrentBaselineMatrixRepository currentBaselineMatrixRepository;
    private final DiscountSegmentsRepository discountSegmentsRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Посмотреть список всех матриц")
    public AllMatrixResponse getMatrices() {
        AllMatrixResponse allMatrixRequest = new AllMatrixResponse();
        allMatrixRequest.setBaselineMatrices(sourceBaselineRepository.findAll().stream().map(s -> new Matrix(s.getName())).toList());
        allMatrixRequest.setDiscountMatrices(discountBaselineRepository.findAll().stream().map(s -> new Matrix(s.getName())).toList());
        return allMatrixRequest;
    }

    @PostMapping(value = "/{matrix_name}", produces = "application/json", consumes = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Установить изменения в матрице из CSV файла. discount_matrix_new - создает новую чистую матрицу. data - если пуст, то изменений не будет.")
    public ResponseEntity<String> setChangesInMatrix(@PathVariable("matrix_name") String name, @RequestBody(required = false) String data) {
        String newName;
        if (data == null) data = "";
        if (name.contains("baseline_matrix")) {
            if (sourceBaselineRepository.findByName(name) == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Матрицы с таким именем не существует\" }");
            newName = getNewNameBaselineMatrix();
        } else if (name.equals("discount_matrix_new")) {
            newName = getNewNameDiscountMatrix();
        } else if (name.contains("discount_matrix")) {
            if (discountBaselineRepository.findByName(name) == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Матрицы с таким именем не существует\" }");
            newName = getNewNameDiscountMatrix();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Неверное имя матрицы\" }");
        }
        List<Integer> notCompletedRows = new ArrayList<>();
        try {
            int counter = 0;
            if (name.equals("discount_matrix_new")) jdbcTemplate.update("create table " + newName + " (microcategory_id int, location_id int, price int);");
            else jdbcTemplate.update("create table " + newName + " as select * from " + name);
            jdbcTemplate.update("ALTER TABLE " + newName + " ADD CONSTRAINT " + newName + "_pkey PRIMARY KEY (location_id, microcategory_id);");
            if (name.contains("baseline_matrix")) {
                sourceBaselineRepository.save(new SourceBaseline(newName));
            } else if (name.contains("discount_matrix")) {
                discountBaselineRepository.save(new DiscountBaseline(newName));
            }
            String query = "insert into " + newName + " (microcategory_id, location_id, price) values ";
            for (String row : data.split("\n")) {
                if (data.isEmpty()) break;
                counter++;
                var slt = row.split(",");
                String microcategory_id, location_id, price;
                try {
                    microcategory_id = slt[0];
                    location_id = slt[1];
                    price = slt[2];
                    Integer.parseInt(microcategory_id);
                    Integer.parseInt(location_id);
                    if (!price.equals("null")) Double.parseDouble(price);
                } catch (Exception e) {
                    notCompletedRows.add(counter);
                    continue;
                }
                query += "(" + microcategory_id + ", " + location_id + ", " + price + "), ";
//                int count = jdbcTemplate.queryForObject("select count(*) from " + newName + " where microcategory_id=" + microcategory_id + " and location_id=" + location_id, Integer.class);
//                if (count > 0) {
//                    jdbcTemplate.update("update " + newName + " SET price=" + price +
//                            " where microcategory_id=" + microcategory_id + " and location_id=" + location_id);
//                } else {
//                    jdbcTemplate.update("insert into " + newName + " (microcategory_id, location_id, price) values (" + microcategory_id + ", " + location_id + ", " + price + ")");
//                }
            }
            query = query.substring(0, query.length() - 2) + " ";
            query += "ON CONFLICT (microcategory_id, location_id) DO UPDATE SET price = EXCLUDED.price;";
            jdbcTemplate.update(query);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \" Неверный формат тела запроса. \" }");
        }
        if (!notCompletedRows.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    "{ \"message\": \"Матрица склонирована, но не все строки были применены.\", " +
                    "\"matrixName\": \"" + newName + "\", " +
                    "\"errorValues\": \"Неверные значения в строках: " + notCompletedRows.stream().map(String::valueOf).collect(Collectors.joining(", ")) + "\" }");
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                "{ \"message\": \"Матрица " + newName + " склонирована успешно.\", " +
                        "\"matrixName\": \"" + newName + "\", " +
                        "\"errorValues\": null }");
    }

    private String getNewNameDiscountMatrix() {
        Long id = discountBaselineRepository.findMaxId();
        return "discount_matrix_" + (id + 1L);
    }

    private String getNewNameBaselineMatrix() {
        Long id = sourceBaselineRepository.findMaxId();
        return "baseline_matrix_" + (id + 1L);
    }

    @GetMapping(value = "/setup", produces = "application/json")
    @Operation(summary = "Посмотреть текущую стандартную матрицу и список скидочных матриц")
    public SetupMatrixResponse setup() {
        SetupMatrixResponse setupMatrixRequest = new SetupMatrixResponse();
        CurrentBaselineMatrix currentBaselineMatrix = currentBaselineMatrixRepository.findAll().get(0);
        setupMatrixRequest.setBaselineMatrix(new Matrix(currentBaselineMatrix.getName()));
        setupMatrixRequest.setDiscountSegments(discountSegmentsRepository.findAllByOrderByIdAsc());
        return setupMatrixRequest;
    }

    @PostMapping(value = "/setup", produces = "application/json")
    @Operation(summary = "Установить текущую стандартную матрицу по имени")
    public ResponseEntity<String> setup(@RequestBody SetupMatrixRequest request) {
        SourceBaseline sourceBaseline = sourceBaselineRepository.findByName(request.getName());
        if (sourceBaseline == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"message\": \"Матрица с таким именем не найдена\" }");
        }
        CurrentBaselineMatrix currentBaselineMatrix = currentBaselineMatrixRepository.findAll().get(0);
        currentBaselineMatrix.setName(sourceBaseline.getName());
        currentBaselineMatrixRepository.save(currentBaselineMatrix);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/setup/segments", produces = "application/json")
    @Operation(summary = "Установить в дискаунт группах матрицы по id сгемента и name discount_table")
    public ResponseEntity<String> setupSegment(@RequestBody SetupDiscountSegmentsRequest request) {
        if (request.getDiscountSegments().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"message\": \"Список сегментов пуст\" }");
        }
        Optional<DiscountSegment> discountSegment;
        DiscountBaseline discountBaseline;
        for (var pair : request.getDiscountSegments()) {
            discountSegment = discountSegmentsRepository.findById(pair.getSegmentId());
            if (discountSegment.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{ \"message\": \"Сегмент с идентификатором " + pair.getSegmentId() + " не найден\" }");
            discountBaseline = discountBaselineRepository.findByName(pair.getDiscountMatrixName());
            if (pair.getDiscountMatrixName() != null) {
                if (!pair.getDiscountMatrixName().equals("null")) {
                    if (discountBaseline == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("{ \"message\": \"Матрица с именем " + pair.getDiscountMatrixName() + " не найдена\" }");
                    discountSegment.get().setName(discountBaseline.getName());
                }
            }
        }
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.executeWithoutResult(status -> {
                for (var pair : request.getDiscountSegments()) {
                    DiscountSegment ds = discountSegmentsRepository.findById(pair.getSegmentId()).get();
                    if (pair.getDiscountMatrixName() == null) ds.setName(null);
                    else if (pair.getDiscountMatrixName().equals("null")) ds.setName(null);
                    else ds.setName(pair.getDiscountMatrixName());
                    discountSegmentsRepository.save(ds);
                }
                for (var pair : request.getDiscountSegments()) {
                    if (pair.getDiscountMatrixName() == null) continue;
                    if (pair.getDiscountMatrixName().equals("null")) continue;
                    if (discountSegmentsRepository.findAllByName(pair.getDiscountMatrixName()).size() > 1) {
                        throw new RuntimeException();
                    }
                }
            });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Нарушена уникальность имён скидочных матриц. 1 - сегмент, одна скидочная матрица или null\" }");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
