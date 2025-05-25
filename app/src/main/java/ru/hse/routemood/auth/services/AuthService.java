package ru.hse.routemood.auth.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hse.routemood.auth.domain.dto.AuthRequest;
import ru.hse.routemood.auth.domain.dto.AuthResponse;
import ru.hse.routemood.auth.domain.dto.RefreshRequest;
import ru.hse.routemood.auth.domain.dto.RegisterRequest;
import ru.hse.routemood.auth.domain.models.Role;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.services.UserService;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationProvider authenticationProvider;

    public AuthResponse registerUser(RegisterRequest request) {
        User user = User.builder()
            .login(request.getLogin())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();

        user.setToken(jwtService.generateRefreshToken());
        user = userService.createUser(user);
        if (user == null) {
            return null;
        }

        return new AuthResponse(jwtService.generateAccessToken(user), user.getToken());
    }

    public AuthResponse loginUser(AuthRequest request) {
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        ));

        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return null;
        }

        return new AuthResponse(jwtService.generateAccessToken(user), user.getToken());
    }

    public AuthResponse refreshTokens(RefreshRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return null;
        }

        if (!user.getToken().equals(request.getRefreshToken())) {
            return null;
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken();

        user.setToken(refreshToken);
        userService.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }
}
