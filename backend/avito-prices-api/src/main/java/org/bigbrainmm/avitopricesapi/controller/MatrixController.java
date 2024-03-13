package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.*;
import org.bigbrainmm.avitopricesapi.entity.DiscountBaseline;
import org.bigbrainmm.avitopricesapi.entity.SourceBaseline;
import org.bigbrainmm.avitopricesapi.repository.DiscountBaselineRepository;
import org.bigbrainmm.avitopricesapi.repository.SourceBaselineRepository;
import org.bigbrainmm.avitopricesapi.service.UpdateBaselineAndSegmentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.bigbrainmm.avitopricesapi.StaticStorage.baselineMatrixAndSegments;
import static org.bigbrainmm.avitopricesapi.StaticStorage.isAvailable;

@RequiredArgsConstructor
@RestController
@Tag(name = "Работа с матрицами", description = "Тут располагаются методы для установки, обновления, загрузки, клонирования, просмотра и многих других операций над матрицами")
@RequestMapping("/api/matrices")
public class MatrixController {

    private final DiscountBaselineRepository discountBaselineRepository;
    private final SourceBaselineRepository sourceBaselineRepository;
    private final UpdateBaselineAndSegmentsService updateBaselineAndSegmentsService;
    private final Logger logger = LoggerFactory.getLogger(MatrixController.class);

    @GetMapping(value = "/setup", produces = "application/json")
    @Operation(summary = "Посмотреть текущую стандартную матрицу и список скидочных матриц")
    public BaselineMatrixAndSegments setup() {
        return baselineMatrixAndSegments;
    }

    @PostMapping(value = "/setup/baseline", produces = "application/json")
    @Operation(summary = "Установить текущую стандартную матрицу по имени")
    public ResponseEntity<MessageResponse> setupBaseline(@RequestBody SetupMatrixRequest request) {
        isAvailable.set(false);
        logger.info("Поменян статус сервера: " + isAvailable.get());
        SourceBaseline sourceBaseline = sourceBaselineRepository.findByName(request.getName());
        if (sourceBaseline == null) {
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Матрица с именем " + request.getName() + " не найдена"));
        }
        baselineMatrixAndSegments.setBaselineMatrix(new Matrix(sourceBaseline.getName()));

        if (!updateBaselineAndSegmentsService.isDataUpdated(baselineMatrixAndSegments)) {
            updateBaselineAndSegmentsService.startUpdatingThread();
        } else {
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
        }

        // Сохранение изменений в локальное хранилище
        StaticStorage.saveBaselineAndSegments(baselineMatrixAndSegments);
        return ResponseEntity.ok(new MessageResponse("Матрица " + request.getName() + " установлена"));
    }

    @PostMapping(value = "/setup/segments", produces = "application/json")
    @Operation(summary = "Установить в дискаунт группах матрицы по id сгемента и name discount_table")
    public ResponseEntity<MessageResponse> setupSegments(@RequestBody SetupDiscountSegmentsRequest request) {
        isAvailable.set(false);
        logger.info("Поменян статус сервера: " + isAvailable.get());
        if (request.getDiscountSegments().isEmpty()) {
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Список сегментов пуст"));
        }
        Optional<DiscountSegment> discountSegment;
        DiscountBaseline discountBaseline;
        for (var pair : request.getDiscountSegments()) {
            discountSegment = baselineMatrixAndSegments.getDiscountSegments().stream().filter(ds -> Objects.equals(ds.getId(), pair.getId())).findAny();
            if (discountSegment.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Сегмент с идентификатором " + pair.getId() + " не найден"));
            discountBaseline = discountBaselineRepository.findByName(pair.getName());
            if (pair.getName() != null) {
                if (!pair.getName().equals("null")) {
                    if (discountBaseline == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new MessageResponse("Матрица с именем \" + pair.getDiscountMatrixName() + \" не найдена"));
                }
            }
        }

        List<DiscountSegment> copy = new ArrayList<>();
        for (var ds : baselineMatrixAndSegments.getDiscountSegments()) copy.add(new DiscountSegment(ds.getId(), ds.getName()));

        for (var pair : request.getDiscountSegments()) {
            DiscountSegment ds = copy.stream().filter(ds1 -> Objects.equals(ds1.getId(), pair.getId())).findAny().get();
            if (pair.getName() == null) ds.setName(null);
            else if (pair.getName().equals("null")) ds.setName(null);
            else ds.setName(pair.getName());
        }
        // Проверка уникальности
        for (var pair : request.getDiscountSegments()) {
            if (pair.getName() == null || pair.getName().equals("null")) continue;
            if (copy.stream().filter(ds1 -> Objects.equals(ds1.getName(), pair.getName())).count() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Нарушена уникальность имён скидочных матриц. 1 - сегмент, одна скидочная матрица или null"));
            }
        }

        if (!updateBaselineAndSegmentsService.isDataUpdated(baselineMatrixAndSegments)) {
            updateBaselineAndSegmentsService.startUpdatingThread();
        } else {
            isAvailable.set(true);
            logger.info("Поменян статус сервера: " + isAvailable.get());
        }

        // Сохранение изменений
        baselineMatrixAndSegments.setDiscountSegments(copy);
        StaticStorage.saveBaselineAndSegments(baselineMatrixAndSegments);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/setup/baseline_segments", produces = "application/json")
    @Operation(summary = "Установить текущую стандартную матрицу по имени и одновременно обновить группы скидочных сегментов. " +
            "По сути объединённый запрос /setup и /setup/segments")
    public ResponseEntity<MessageResponse> setupBaselineSegments(@RequestBody BaselineMatrixAndSegments request) {
        // isAvailable устаналивают методы

        ResponseEntity<MessageResponse> baselineResponse = setupBaseline(new SetupMatrixRequest(request.getBaselineMatrix().getName()));
        if (baselineResponse.getStatusCode() != HttpStatus.OK) return baselineResponse;

        ResponseEntity<MessageResponse> segmentsResponse = setupSegments(new SetupDiscountSegmentsRequest(request.getDiscountSegments()));
        if (segmentsResponse.getStatusCode() != HttpStatus.OK) return segmentsResponse;

        return ResponseEntity.ok(new MessageResponse("Матрицы и сегменты успешно обновлены"));
    }
}
