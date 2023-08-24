package com.backend.curi.common.interceptor;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.service.LaunchedWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class LaunchedworkflowAuthInterceptor implements HandlerInterceptor {

    private final LaunchedWorkflowService launchedWorkflowService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
        Long launchedworkflowId = Extractor.extractLongFromUrl(request, "launchedworkflows");


        var launchedWorkflow = launchedWorkflowService.getLaunchedWorkflowEntity(launchedworkflowId);
        if(!launchedWorkflow.getWorkspace().getId().equals(workspaceId)){
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);
        }

        return true;
    }

}

