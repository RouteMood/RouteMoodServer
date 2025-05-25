package ru.hse.routemood.google.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hse.routemood.auth.domain.dto.AuthResponse;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.user.services.UserService;
import ru.hse.routemood.google.domain.dto.GoogleAuthRequest;

@Service
public class GoogleService {

    @Value("${application.google.client-id}")
    private String googleClientId;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public ResponseEntity<AuthResponse> ValidateAndProcessToken(GoogleAuthRequest request) {
        String googleToken = request.getGoogleToken();
        GoogleIdToken.Payload payload;
        try {
            payload = verifyGoogleToken(googleToken);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (payload == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String username = payload.getSubject();
        String login = payload.getEmail();

        User user = userService.createOrSave(User.builder()
            .login(login)
            .username(username)
            .token(jwtService.generateRefreshToken())
            .build()
        );
        return new ResponseEntity<>(new AuthResponse(jwtService.generateAccessToken(user), user.getToken()), HttpStatus.OK);
    }

    private GoogleIdToken.Payload verifyGoogleToken(String googleToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(googleClientId))
            .build();

        GoogleIdToken idToken = verifier.verify(googleToken);
        if (idToken != null) {
            return idToken.getPayload();
        }
        return null;
    }
}
