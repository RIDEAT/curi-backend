package com.backend.curi.common;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class WorkspaceAuthInterceptor implements HandlerInterceptor {

    private final UserworkspaceService userworkspaceService;
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Long workspaceId = extractWorkspaceId(request);
    userworkspaceService.belongstoWorkspace(workspaceId);

    return true;
    }

    public static Long extractWorkspaceId(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        String[] parts = requestUrl.split("/workspaces/");
        if (parts.length >= 2) {
            String workspaceIdStr = parts[1].split("/")[0];
            try {
                return Long.parseLong(workspaceIdStr);
            } catch (NumberFormatException e) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
            }
        }
        // Return a default value or throw an exception if the workspaceId cannot be extracted.
        // For simplicity, let's return null if the workspaceId cannot be extracted.
        return null;
    }
}
