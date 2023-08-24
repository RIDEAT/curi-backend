package com.backend.curi.common.interceptor;

import com.backend.curi.userworkspace.service.UserworkspaceService;
import lombok.RequiredArgsConstructor;
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
    Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
    userworkspaceService.belongstoWorkspace(workspaceId);

    return true;
    }

}
