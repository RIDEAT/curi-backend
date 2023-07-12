package com.backend.curi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrentUser {
    String userEmail, userId, newAuthToken;

    public CurrentUser(){}

}
