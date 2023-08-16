package com.backend.curi.common.interceptor;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.service.LaunchedSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class LaunchedsequenceAuthInterceptor implements HandlerInterceptor {

    private final LaunchedSequenceService launchedSequenceService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
        Long launchedsequenceId = Extractor.extractLongFromUrl(request, "sequences");

        var launchedSequence = launchedSequenceService.getLaunchedSequenceEntity(launchedsequenceId);
        if(!launchedSequence.getWorkspace().getId().equals(workspaceId)){
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.SEQUENCE_NOT_EXISTS);
        }
        return true;
    }

}
