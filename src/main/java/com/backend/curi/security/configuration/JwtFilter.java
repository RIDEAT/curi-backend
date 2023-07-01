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
    @Value("${jwt.secret}")
    private final String secretKey;


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
        String token = authorization.split(" ")[1];

        log.info("발급받은 토큰: {}" , token);
        log.info("secret key: {}", secretKey);


        // Token expired 되었는지 여부
        // Token 유효성 검사해야함

        if(JwtUtil.isExpired(token, secretKey)){
            log.error("token이 만료되었습니다. ", authorization);
            filterChain.doFilter(request, response);
            return;
        }

        // UserName Token에서 꺼내기


        String userName = "";

        // 권한 부여

        //credential 함 보기
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority(("USER"))));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

    }
}

