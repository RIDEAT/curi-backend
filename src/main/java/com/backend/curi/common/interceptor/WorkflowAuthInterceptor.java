package com.backend.curi.common.interceptor;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkflowAuthInterceptor implements HandlerInterceptor {

    private final WorkflowService workflowService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
        Long workflowId = Extractor.extractLongFromUrl(request, "workflows");

        List<WorkflowResponse> workflowResponseList = workflowService.getWorkflows(workspaceId);
        boolean found = workflowResponseList.stream()
                .anyMatch(workflowResponse -> workflowResponse.getId().equals(workflowId));

        if (!found) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);

        return true;
    }


}
