package org.bigbrainmm.avitoadminapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitoadminapi.dto.JwtAuthenticationResponse;
import org.bigbrainmm.avitoadminapi.dto.MessageResponse;
import org.bigbrainmm.avitoadminapi.dto.SignInRequest;
import org.bigbrainmm.avitoadminapi.dto.SignUpRequest;
import org.bigbrainmm.avitoadminapi.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для аутентификации. Просмотр подробностей и тестирование в swagger-ui: http://localhost:PORT/swagger-ui/index.html
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация (не обязательно)",
        description = "Методы для аутентификации. " +
        "Работают через JWT токены. " +
        "Сказали, что она не нужна, потому методы как бы работают, " +
        "но все равно разрешен маппинг на все запросы мимо авторизации, " +
        "так что можно и не авторизовываться :)")
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

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse error(RuntimeException ex) {
        return new MessageResponse(ex.getMessage());
    }
}