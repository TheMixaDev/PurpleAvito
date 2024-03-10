package org.bigbrainmm.avitopricesapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitopricesapi.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello-world")
@RequiredArgsConstructor
@Tag(name = "Привет, Мир!")
public class HelloWorldController {
    private final UserService service;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Выводит сообщение hello world")
    public String example() {
        return "{ \"message\": \"Hello, world!\" }";
    }
}