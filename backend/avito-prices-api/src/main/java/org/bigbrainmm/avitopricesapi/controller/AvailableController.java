package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.bigbrainmm.avitopricesapi.StaticStorage.isAvailable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/available")
@Tag(name = "Проверка доступности сервиса", description = "Доступность сервиса определяет то, может ли он сейчас выдать корректную цену или нет.")
public class AvailableController {
    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<MessageResponse> available() {
        if (isAvailable.get()) return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Сервис доступен"));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new MessageResponse("Сервис недоступен"));
    }
}
