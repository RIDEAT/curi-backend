package com.backend.curi.common.interceptor;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.service.ModuleService;
import com.backend.curi.workflow.repository.entity.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class ModuleAuthInterceptor implements HandlerInterceptor {

    private final ModuleService moduleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
        Long moduleId = Extractor.extractLongFromUrl(request, "modules");

        Module module = moduleService.getModuleEntity(moduleId);

        if (!workspaceId.equals(module.getWorkspace().getId())){
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS);
        }

        return true;
    }


}
