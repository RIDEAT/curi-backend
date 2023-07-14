package com.backend.curi.user.controller.dto;


import com.backend.curi.user.repository.entity.User_;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserListResponse {
    private String status;
    List<UserResponse> userList;


    public static UserListResponse ofSuccess(List<User_> userList) {
        var responseList = userList.stream()
                .map(UserResponse::ofSuccess)
                .collect(Collectors.toList());
        return new UserListResponse("success", responseList);
    }
}
