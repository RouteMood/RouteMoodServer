package ru.hse.routemood.auth;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.auth.domain.dto.AuthRequest;
import ru.hse.routemood.auth.domain.dto.AuthResponse;
import ru.hse.routemood.auth.domain.dto.RefreshRequest;
import ru.hse.routemood.auth.domain.dto.RegisterRequest;
import ru.hse.routemood.auth.services.AuthService;

@RestController
@RequestMapping(path = "/api")
@AllArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request) {
        log.info("Get request: {}", request.getLogin());
        AuthResponse result = authService.registerUser(request);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        log.info("Get request: {}", request.getLogin());
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest request) {
        AuthResponse result = authService.loginUser(request);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody RefreshRequest request) {
        AuthResponse result = authService.refreshTokens(request);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(result);
    }
}
