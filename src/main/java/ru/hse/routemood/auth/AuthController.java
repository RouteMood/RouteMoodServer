package ru.hse.routemood.auth;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.auth.models.AuthRequest;
import ru.hse.routemood.auth.models.RegisterRequest;
import ru.hse.routemood.auth.models.Role;
import ru.hse.routemood.auth.models.User;
import ru.hse.routemood.auth.repository.UserServiceRepository;
import ru.hse.routemood.auth.services.JwtService;

@RestController
@RequestMapping(path = "/api/auth")
@AllArgsConstructor
public class AuthController {


    private final UserServiceRepository userServiceRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(RequestEntity<RegisterRequest> request) {
        if (request.getBody() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User newUser = User.builder()
            .login(request.getBody().getLogin())
            .password(passwordEncoder.encode(request.getBody().getPassword()))
            .role(Role.USER)
            .build();

        userServiceRepository.save(newUser);
        return ResponseEntity.ok(jwtService.generateAccessToken(newUser));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> loginUser(RequestEntity<AuthRequest> request) {
        if (request.getBody() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userServiceRepository.findByLogin(request.getBody().getLogin()).orElse(null);
        System.out.println(user.getLogin() + " " + user.getPassword());
        if (user == null || !user.getPassword().equals(request.getBody().getPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        return ResponseEntity.ok(jwtService.generateAccessToken(user));
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userServiceRepository.findAll();
        return ResponseEntity.ok(users);
    }
}