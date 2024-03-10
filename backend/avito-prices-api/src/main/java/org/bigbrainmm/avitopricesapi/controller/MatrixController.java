package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/matrices")
@RestController
@Tag(name = "Работа с матрицами")
public class MatrixController {

    private final DiscountBaselineRepository discountBaselineRepository;
    private final SourceBaselineRepository sourceBaselineRepository;
    private final CurrentBaselineMatrixRepository currentBaselineMatrixRepository;
    private final DiscountSegmentsRepository discountSegmentsRepository;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Посмотреть список всех матриц")
    public AllMatrixResponse getMatrices() {
        AllMatrixResponse allMatrixRequest = new AllMatrixResponse();
        allMatrixRequest.setBaselineMatrices(sourceBaselineRepository.findAll().stream().map(s -> new Matrix(s.getName())).toList());
        allMatrixRequest.setDiscountMatrices(discountBaselineRepository.findAll().stream().map(s -> new Matrix(s.getName())).toList());
        return allMatrixRequest;
    }

    @GetMapping(value = "/setup", produces = "application/json")
    @Operation(summary = "Посмотреть текущую стандартную матрицу и список скидочных матриц")
    public SetupMatrixResponse setup() {
        SetupMatrixResponse setupMatrixRequest = new SetupMatrixResponse();
        CurrentBaselineMatrix currentBaselineMatrix = currentBaselineMatrixRepository.findAll().get(0);
        setupMatrixRequest.setBaselineMatrix(new Matrix(currentBaselineMatrix.getName()));
        setupMatrixRequest.setDiscountSegments(discountSegmentsRepository.findAll());
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
        if (request.getDiscountSegment().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"message\": \"Список сегментов пуст\" }");
        }
        for (var pair : request.getDiscountSegment()) {
            Optional<DiscountSegment> discountSegment = discountSegmentsRepository.findById(pair.getSegmentId());
            if (discountSegment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"message\": \"Сегмент с идентификатором " + pair.getSegmentId() + " не найден\" }");
            }
            DiscountBaseline discountBaseline = discountBaselineRepository.findByName(pair.getDiscountMatrixName());
            if (discountBaseline == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"message\": \"Матрица с именем " + pair.getDiscountMatrixName() + " не найдена\" }");
            }
            discountSegment.get().setName(discountBaseline.getName());
            discountSegmentsRepository.save(discountSegment.get());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
