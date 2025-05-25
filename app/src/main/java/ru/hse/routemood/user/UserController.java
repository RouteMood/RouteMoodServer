package ru.hse.routemood.user;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;

@RestController
@RequestMapping(path = "/api")
@AllArgsConstructor
public class UserController {


    private final UserServiceRepository userServiceRepository;

    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userServiceRepository.findAll();
        return ResponseEntity.ok(users);
    }
}