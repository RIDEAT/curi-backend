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
    private String status;
    private String id;
    private String email;

    public static UserResponse ofSuccess(User_ user) {
        return new UserResponse("success",
                user.getUserId(),
                user.getEmail());
    }
}
