package com.backend.curi.common.interceptor;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.service.LaunchedModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class LaunchedmoduleAuthInterceptor implements HandlerInterceptor {
    private final LaunchedModuleService launchedModuleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
        Long launchedmoduleId = Extractor.extractLongFromUrl(request, "modules");

        var launchedModule = launchedModuleService.getLaunchedModuleEntity(launchedmoduleId);
        if(!launchedModule.getWorkspace().getId().equals(workspaceId)){
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS);
        }

        return true;
    }

}
