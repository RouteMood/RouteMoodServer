package ru.hse.routemood.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@AllArgsConstructor
public class AnotherController {

    @GetMapping(path = "/hell2")
    public ResponseEntity<String> listUsers() {
        return ResponseEntity.ok("hello");
    }
}