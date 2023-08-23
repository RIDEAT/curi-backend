package com.backend.curi.common.interceptor;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class SlackMemberAuthInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        request.getHeader(HttpHeaders.AUTHORIZATION);
        // 헤더 authorization의 frontofficeId와 memberId 검증하는 로직

        return true;
    }


}
