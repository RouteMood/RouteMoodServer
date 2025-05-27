package ru.hse.routemood.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RefreshRequest {

    private String accessToken;
    private String refreshToken;
    private String username;
}
