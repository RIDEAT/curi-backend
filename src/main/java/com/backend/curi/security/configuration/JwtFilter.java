package com.backend.curi.security.configuration;


import com.backend.curi.security.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {


    //private final UserService userService;
    private final String authSecretKey;
    private final String refreshSecretKey;



    // 권한을 부여.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization: {}", authorization);

        if (authorization == null || !authorization.startsWith("Bearer ")){
            log.error("authorization 을 잘못 보냈습니다. ", authorization);
            filterChain.doFilter(request, response);
            return;
        }


        // Token 꺼내기
        String authToken = authorization.split(" ")[1];

        log.info("발급받은 토큰: {}" , authToken);


        // authToken 유효성 검사
        if (JwtUtil.isInvalid(authToken, authSecretKey)){
            log.error("auth token이 유효하지 않습니다. ", authToken);
            filterChain.doFilter(request, response);
            return;
        }

        // authToken expired 되었는지 여부
        if(JwtUtil.isExpired(authToken, authSecretKey)){
            log.error("auth token이 만료되었습니다. ", authToken);
            filterChain.doFilter(request, response);
            return;
        }

        // UserId Token에서 꺼내기

        String userId = JwtUtil.getUserId(authToken, authSecretKey);
        log.info("userId: {}", userId);

        // 권한 부여

        //credential 함 보기
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(("USER"))));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}

