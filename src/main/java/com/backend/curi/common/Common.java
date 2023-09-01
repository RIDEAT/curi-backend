package com.backend.curi.common;

import com.backend.curi.exception.CuriException;
import com.backend.curi.security.dto.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Common {
    public CurrentUser getCurrentUser(){
        try {
            return (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            CurrentUser currentUser = new CurrentUser();
            currentUser.setUserId("no-cuurent-user");
            currentUser.setNewAuthToken("no-cuurent-user");
            return currentUser;
        }
    }
}
