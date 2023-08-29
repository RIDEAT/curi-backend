package com.backend.curi.user.controller.dto;

import com.backend.curi.user.repository.entity.User_;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String userId;

    public static UserResponse of(User_ user) {
        return new UserResponse(user.getUserId());
    }
}
