package com.backend.curi.user.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class UserResponse {

    private String id;
    private String email;

    @Builder
    public UserResponse(String id, String email){
        this.id = id;
        this.email = email;
    }
}