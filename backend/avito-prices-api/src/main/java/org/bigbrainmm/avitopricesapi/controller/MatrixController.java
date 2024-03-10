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
import java.util.List;
import java.util.Optional;
import java.util.Stack;

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

    @Transactional
    @PostMapping(value = "/{matrix_name}", produces = "application/json")
    @Operation(summary = "Установить изменения в матрице из CSV файла")
    public ResponseEntity<String> setChangesInMatrix(@PathVariable("matrix_name") String name, @RequestBody String data) {
        String newName;
        if (name.contains("baseline_matrix")) {
            if (sourceBaselineRepository.findByName(name) == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Матрицы с таким именем не существует\" }");
            SourceBaseline sourceBaseline = sourceBaselineRepository.findFirstByOrderByNameDesc();
            int id = 0;
            try {
                if (sourceBaseline != null) id = Integer.parseInt(sourceBaseline.getName().split("_")[2]);
            } catch (Exception ignored) { }
            id++;
            newName = "discount_matrix_" + id;
        } else if (name.contains("discount_matrix")) {
            if (discountBaselineRepository.findByName(name) == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Матрицы с таким именем не существует\" }");
            DiscountBaseline discountBaseline = discountBaselineRepository.findFirstByOrderByNameDesc();
            int id = 0;
            try {
                if (discountBaseline != null) id = Integer.parseInt(discountBaseline.getName().split("_")[2]);
            } catch (Exception ignored) { }
            id++;
            newName = "discount_matrix_" + id;
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Неверное имя матрицы\" }");
        }
        try {
            jdbcTemplate.update("create table " + newName + " as select * from " + name);
            for (String row : data.split("\n")) {
                var slt = row.split(",");
                String microcategory_id = slt[0];
                String location_id = slt[1];
                String price = slt[2];
                int count = jdbcTemplate.queryForObject("select count(*) from " + newName + " where microcategory_id=" + microcategory_id + " and location_id=" + location_id, Integer.class);
                if (count > 0) {
                    jdbcTemplate.update("update " + newName + " SET price=" + price +
                            " where microcategory_id=" + microcategory_id + " and location_id=" + location_id);
                } else {
                    jdbcTemplate.update("insert into " + newName + " (microcategory_id, location_id, price) values (" + microcategory_id + ", " + location_id + ", " + price + ")");
                }
            }
            if (name.contains("baseline_matrix")) {
                sourceBaselineRepository.save(new SourceBaseline(newName));
            } else if (name.contains("discount_matrix")) {
                discountBaselineRepository.save(new DiscountBaseline(newName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \" Неверный формат тела запроса. \" }");
        }
        return ResponseEntity.ok("{ \"message\": \"Матрица " + newName + " склонирована с изменениями успешно\" }");
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
