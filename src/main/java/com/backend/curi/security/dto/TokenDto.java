package com.backend.curi.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {
    private String authToken;
    private String refreshToken;

    public TokenDto(String authToken, String refreshToken){
        this.authToken = authToken;
        this.refreshToken = refreshToken;
    }
}
