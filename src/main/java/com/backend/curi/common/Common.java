package com.backend.curi.common;

import com.backend.curi.exception.CuriException;
import com.backend.curi.security.dto.CurrentUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Common {
    @Value("${workplug.view.url}")
    private String viewPath;
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

    public String getFrontOfficeUrl(UUID id, UUID accessToken) {
        return viewPath + "/"+ id + "?token=" + accessToken;
    }
}
