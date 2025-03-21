package ru.hse.routemood.gpt;

import lombok.Getter;

public class TokenStore {

    @Getter
    private final String token;

    public TokenStore(String token) {
        this.token = token;
    }
}
