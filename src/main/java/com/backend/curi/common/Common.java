package com.backend.curi.common;

import com.backend.curi.security.dto.CurrentUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Common {
    public CurrentUser getCurrentUser(){
        return (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
