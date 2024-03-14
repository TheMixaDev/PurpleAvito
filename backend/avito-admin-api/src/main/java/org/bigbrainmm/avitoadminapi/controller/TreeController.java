package org.bigbrainmm.avitoadminapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitoadminapi.StaticStorage;
import org.bigbrainmm.avitoadminapi.dto.TreeNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для просмотра дерева категорий и локаций. Просмотр подробностей и тестирование в swagger-ui: http://localhost:PORT/swagger-ui/index.html
 */
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
