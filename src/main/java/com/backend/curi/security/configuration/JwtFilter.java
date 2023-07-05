package com.backend.curi.security.configuration;


import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.security.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.backend.curi.security.configuration.Constants.AUTH_HEADER;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {


    //private final UserService userService;
    private final String authSecretKey;
    private final String refreshSecretKey;
    private final Long authExpiredMs;
    private final Long refreshExpiredMs;




    // 권한을 부여.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // user authorize 할 때는 그냥 패스 ~
        if ("/user/authorize".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("authorization: {}", authorization);


        if (!authorization.startsWith("Bearer ")){
            log.error("authorization 을 잘못 보냈습니다. ", authorization);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            filterChain.doFilter(request, response);

            return;

        }


        // Token 꺼내기
        String authToken = authorization.split(" ")[1];
        String refreshToken = getCookieName(request, "refreshToken");


        log.info("제출한 auth 토큰: {}" , authToken);
        log.info("refresh 토큰: {}",refreshToken);


        // authToken 유효성 검사
        if (!JwtUtil.isValid(authToken, authSecretKey)){
            log.info("auth token이 유효하지 않습니다. : {}", authToken);
            if (JwtUtil.isValid(refreshToken, refreshSecretKey)){
                log.info("refresh token은 유효합니다. : {}", refreshToken);
                log.info("auth token을 발급합니다.");
                String userId = JwtUtil.getUserId(refreshToken, refreshSecretKey);
                String newAuthToken = JwtUtil.createJWT(userId, authSecretKey, authExpiredMs);
                response.setHeader("AuthToken", newAuthToken);

                CurrentUser currentUser = new CurrentUser();
                currentUser.setUserId(userId);
                currentUser.setNewAuthToken(newAuthToken);


                // 여기에 security context 인증 정보 넣어야 할지도 .
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser,null, List.of(new SimpleGrantedAuthority(("USER"))));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                filterChain.doFilter(request, response);
                return;
            } else{
                log.info("refresh token이 유효하지 않습니다. : {}", refreshToken);
                response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
                response.sendError(HttpStatus.NOT_ACCEPTABLE.value(),"refresh token이 유효하지 않습니다.");
                return;
            }
        }



        // UserId Token에서 꺼내기

        String userId = JwtUtil.getUserId(authToken, authSecretKey);
        log.info("userId: {}", userId);

        response.setHeader("AuthToken", authToken);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        // 권한 부여
        //credential 함 보기
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
    public static String getCookieName(HttpServletRequest req,String name) {
        Cookie[] cookies = req.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}

