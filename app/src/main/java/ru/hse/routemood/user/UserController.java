package ru.hse.routemood.user;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.user.domain.dto.UserResponse;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;
import ru.hse.routemood.user.services.UserService;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
@Log4j2
public class UserController {


    private final UserServiceRepository userServiceRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/list")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userServiceRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getInfo/{username}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable String username) {
        User user = userServiceRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUserInfo(username));
    }

    @PostMapping("/update-avatar")
    public ResponseEntity<UUID> updateAvatar(@RequestPart("file") MultipartFile file,
        @RequestHeader("Authorization") String authHeader) {
        String username = getUsername(authHeader);
        try {
            UUID id = userService.updateAvatar(username, file);
            return ResponseEntity.ok(id);
        } catch (IOException e) {
            log.error("Can't update avatar {}", username);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getUsername(String authHeader) {
        return jwtService.extractUsername(authHeader.substring(7));
    }
}