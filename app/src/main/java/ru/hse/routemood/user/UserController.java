package ru.hse.routemood.user;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.user.domain.dto.UserResponse;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;
import ru.hse.routemood.user.services.UserService;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class UserController {


    private final UserServiceRepository userServiceRepository;
    private final UserService userService;

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

    @PostMapping("/avatar/{username}")
    public ResponseEntity<UUID> updateAvatar(@PathVariable String username,
        @RequestPart MultipartFile avatar) {
        try {
            UUID id = userService.updateAvatar(username, avatar);
            return ResponseEntity.ok(id);
        } catch (IOException e) {
            System.out.println("Can't update avatar " + username);
            return ResponseEntity.internalServerError().build();
        }
    }
}