package ru.hse.routemood.google;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.auth.domain.dto.AuthResponse;
import ru.hse.routemood.google.domain.dto.GoogleAuthRequest;
import ru.hse.routemood.google.services.GoogleService;

@RestController
@RequestMapping(path = "/api/google")
@AllArgsConstructor
public class GoogleAuthController {

    @Autowired
    private GoogleService googleService;

    @PostMapping(path = "/auth")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody GoogleAuthRequest request) {
        return googleService.ValidateAndProcessToken(request);
    }
}