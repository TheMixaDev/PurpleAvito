package org.bigbrainmm.avitoadminapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitoadminapi.dto.JwtAuthenticationResponse;
import org.bigbrainmm.avitoadminapi.dto.SignInRequest;
import org.bigbrainmm.avitoadminapi.dto.SignUpRequest;
import org.bigbrainmm.avitoadminapi.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для аутентификации. Работает через JWT токены. " +
        "Но сказали, что она не нужна, потому методы как бы работают, " +
        "но все равно разрешен маппинг на все запросы мимо авторизации.")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(
            @RequestBody @Valid SignUpRequest request
    ) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
            @RequestBody @Valid SignInRequest request
    ) {
        return authenticationService.signIn(request);
    }
}