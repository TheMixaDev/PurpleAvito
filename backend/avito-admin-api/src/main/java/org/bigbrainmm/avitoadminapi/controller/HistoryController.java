package org.bigbrainmm.avitoadminapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitoadminapi.dto.HistoryResponse;
import org.bigbrainmm.avitoadminapi.entity.History;
import org.bigbrainmm.avitoadminapi.repository.HistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для просмотра истории изменений цен. Просмотр подробностей и тестирование в swagger-ui: http://localhost:PORT/swagger-ui/index.html
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/history")
@Tag(name = "Просмотр истории изменений цен")
public class HistoryController {
    private final HistoryRepository historyRepository;
    @GetMapping
    public HistoryResponse getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<History> history = historyRepository.findAll(pageRequest).getContent();
        long total = historyRepository.count();
        return new HistoryResponse(history, total);
    }
}
