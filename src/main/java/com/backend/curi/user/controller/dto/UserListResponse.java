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
    List<UserResponse> userList;


    public static UserListResponse of(List<User_> userList) {
        var responseList = userList.stream()
                .map(UserResponse::of)
                .collect(Collectors.toList());
        return new UserListResponse(responseList);
    }
}
