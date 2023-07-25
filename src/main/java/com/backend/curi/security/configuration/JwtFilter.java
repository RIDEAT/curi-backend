package com.backend.curi.security.configuration;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.backend.curi.security.configuration.Constants.AUTH_HEADER;
import static com.backend.curi.security.configuration.Constants.AUTH_SERVER;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    // 권한을 부여.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
           // supposeThereIsNoIssue(request, response, filterChain);

            // h2-console 할 때는 패스!
            if (request.getRequestURI().startsWith("/h2-console")){
                filterChain.doFilter(request, response);
                return;
            }

            // h2-console 할 때는 패스!
            if (request.getRequestURI().startsWith("/swagger-ui") ){
                filterChain.doFilter(request, response);
                return;
            }

            // h2-console 할 때는 패스!
            if (request.getRequestURI().startsWith("/backend-api-docs") ){
                filterChain.doFilter(request, response);
                return;
            }
            log.info(request.getRequestURI());

            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    log.info("Name: {}" , cookie.getName());
                    log.info("value: {}", cookie.getValue());

                }
            } else log.info("cookie is null");



            ResponseEntity<String> responseEntity = communicateWithAuthServer(request);

            // 여기서 authToken 이랑 refresh Token 담아서 줘야 하나.

            String responseBody = responseEntity.getBody();

            // auth token을 헤더에 담으려면 이렇게 해야되지 않겠나.
            response.setHeader("AuthToken", responseEntity.getHeaders().get("AuthToken").get(0));
            log.info(responseBody);

            // Parse the responseBody JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Extract userId
            String userId = jsonNode.get("userId").asText();
            //String userEmail = jsonNode.get("userEmail").asText();


            CurrentUser currentUser = new CurrentUser();
            currentUser.setUserId(userId);
            currentUser.setNewAuthToken(responseEntity.getHeaders().get("AuthToken").get(0));


            // 여기에 security context 인증 정보 넣어야 할지도 .
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
            return;
        }
        catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        catch (HttpClientErrorException e){
            log.info(e.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            // 응답 헤더에 Content-Type 설정
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            response.setCharacterEncoding("UTF-8");

            // 에러 메시지를 응답 본문에 작성
            response.getWriter().write(ErrorType.AUTH_SERVER_ERROR.getMessage());
            return;
        } catch(CuriException e){

            log.info("curi exception is catched");
            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());
            filterChain.doFilter(request, response);

            return;
        }
    }
    private ResponseEntity communicateWithAuthServer(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpMethod httpMethod = HttpMethod.GET; // 호출할 HTTP 메서드 선택 (GET, POST, 등)
        URI requestUri = URI.create(AUTH_SERVER.concat("/verify"));
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            requestHeaders.add(headerName, headerValue);
        }
        RequestEntity<Void> requestEntity = new RequestEntity<>(requestHeaders, httpMethod, requestUri);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return responseEntity;
    }

    private void supposeThereIsNoIssue(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
        String userId = "sdklnadslfmpasodfkpaoskdf[pasdfa";
        //String userEmail = jsonNode.get("userEmail").asText();


        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);



        // 여기에 security context 인증 정보 넣어야 할지도 .
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
        return;
    }

}

