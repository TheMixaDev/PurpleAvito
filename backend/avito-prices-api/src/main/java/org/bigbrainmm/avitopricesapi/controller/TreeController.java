package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.StaticStorage;
import org.bigbrainmm.avitopricesapi.dto.TreeNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tree")
@Tag(name = "Дерево категорий и локаций")
public class TreeController {

    @Operation(summary = "Посмотреть дерево категорий")
    @GetMapping(value = "/microcategories" , produces = "application/json")
    public TreeNode getMicrocategoryTree() {
        return StaticStorage.microCategoryRoot;
    }

    @Operation(summary = "Посмотреть дерево локаций")
    @GetMapping(value = "/locations" , produces = "application/json")
    public TreeNode getLocationsTree() {
        return StaticStorage.locationsRoot;
    }
}
