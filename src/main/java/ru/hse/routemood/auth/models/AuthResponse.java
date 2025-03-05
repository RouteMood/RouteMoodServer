package ru.hse.routemood.auth.models;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AuthResponse {
    private String token;
    private UUID userId;
}
