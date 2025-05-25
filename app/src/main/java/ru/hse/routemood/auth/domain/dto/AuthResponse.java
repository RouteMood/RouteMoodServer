package ru.hse.routemood.auth.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthResponse {

    private String token;
    private String refreshToken = null;

    public AuthResponse(String token) {
        this.token = token;
    }
}
