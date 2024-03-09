package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.GetMicrocategoryTreeRequest;
import org.bigbrainmm.avitopricesapi.staticstorage.Trees;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tree")
@Tag(name = "Дерево категорий и локаций")
public class TreeController {
    @GetMapping(value = "/microcategories" , produces = "application/json")
    public GetMicrocategoryTreeRequest getMicrocategoryTree() {
        return new GetMicrocategoryTreeRequest(Trees.microCategoryRoot);
    }
    @GetMapping(value = "/locations" , produces = "application/json")
    public GetMicrocategoryTreeRequest getLocationsTree() {
        return new GetMicrocategoryTreeRequest(Trees.locationRoot);
    }
}
