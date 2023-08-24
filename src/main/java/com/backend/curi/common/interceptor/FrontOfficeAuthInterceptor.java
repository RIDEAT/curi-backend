package com.backend.curi.common.interceptor;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.service.FrontOfficeService;
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
public class FrontOfficeAuthInterceptor implements HandlerInterceptor {
    private final FrontOfficeService frontofficeService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UUID frontOfficeId = Extractor.extractUUIDFromUrl(request, "front-offices");
        UUID accessToken = getAccessToken(request);

        frontofficeService.checkAuth(frontOfficeId, accessToken);

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
