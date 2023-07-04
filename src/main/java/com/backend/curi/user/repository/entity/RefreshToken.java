package com.backend.curi.user.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;
    @NotBlank
    private String refreshToken;
    @NotBlank
    private String userId;

    public RefreshToken(String refreshToken, String userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public RefreshToken updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
