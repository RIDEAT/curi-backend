package com.backend.curi.common.interceptor;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.service.FrontofficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FrontofficeAuthInterceptor implements HandlerInterceptor {
    private final FrontofficeService frontofficeService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long workspaceId = Extractor.extractLongFromUrl(request, "workspaces");
        Long launchedmoduleId = Extractor.extractLongFromUrl(request, "modules");
        UUID frontofficeId = Extractor.extractUUIDFromUrl(request, "frontoffices");
        UUID accessToken = getAccessToken(request);

        frontofficeService.checkAuth(frontofficeId, accessToken);

        return true;
    }

    private UUID getAccessToken(HttpServletRequest request){
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.FRONTOFFICE_UNAUTHORIZED);
        }

        String accessToken = authorization.split(" ")[1];

        return UUID.fromString(accessToken);
    }
}
