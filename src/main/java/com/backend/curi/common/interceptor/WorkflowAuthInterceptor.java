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
        Long workspaceId = extractFromUrl(request, "workspaces");
        Long workflowId = extractFromUrl(request, "workflows");

        List<WorkflowResponse> workflowResponseList = workflowService.getWorkflows(workspaceId);
        boolean found = workflowResponseList.stream()
                .anyMatch(workflowResponse -> workflowResponse.getId().equals(workflowId));

        if (!found) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS);

        return true;
    }

    public static Long extractFromUrl(HttpServletRequest request, String point) {
        String requestUrl = request.getRequestURI();
        String[] parts = requestUrl.split("/" +point+ "/");
        if (parts.length >= 2) {
            String workspaceIdStr = parts[1].split("/")[0];
            try {
                return Long.parseLong(workspaceIdStr);
            } catch (NumberFormatException e) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
            }
        }

        throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
    }
}