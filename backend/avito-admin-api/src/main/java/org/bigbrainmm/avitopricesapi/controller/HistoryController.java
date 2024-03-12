package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.entity.History;
import org.bigbrainmm.avitopricesapi.repository.HistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/history")
@Tag(name = "Просмотр истории изменений цен")
public class HistoryController {
    private final HistoryRepository historyRepository;
    @GetMapping
    public List<History> getHistory() {
        return historyRepository.findAll();
    }
}
