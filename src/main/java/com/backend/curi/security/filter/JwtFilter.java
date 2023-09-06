package com.backend.curi.security.filter;


import com.backend.curi.common.configuration.Constants;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.service.WorkflowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final UserService userService;
    private final Constants constants;
    // 권한을 부여.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (request.getRequestURI().startsWith("/h2-console")){
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getRequestURI().startsWith("/swagger-ui") ){
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getRequestURI().startsWith("/backend-api-docs") ){
                filterChain.doFilter(request, response);
                return;
            }


            if (request.getRequestURI().startsWith("/front-offices") ){
                filterChain.doFilter(request, response);
                return;
            }


            if (request.getRequestURI().startsWith("/health") ){
                filterChain.doFilter(request, response);
                return;
            }







            if(constants.getENV().equals("local") || constants.getENV().equals("data-local") || constants.getENV().equals("data-build")) {
                pretendTobeAuthorized(request, response, filterChain);
            }
            else {
                ResponseEntity<String> responseEntity = communicateWithAuthServer(request);

                // 여기서 authToken 이랑 refresh Token 담아서 줘야 하나.

                String responseBody = responseEntity.getBody();

                // auth token을 헤더에 담으려면 이렇게 해야되지 않겠나.
                response.setHeader("AuthToken", responseEntity.getHeaders().get("AuthToken").get(0));

                // Parse the responseBody JSON string
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // Extract userId
                String userId = jsonNode.get("userEmail").asText();
                //String userEmail = jsonNode.get("userEmail").asText();


                CurrentUser currentUser = new CurrentUser();
                currentUser.setUserId(userId);
                currentUser.setName("mock name");
                currentUser.setNewAuthToken(responseEntity.getHeaders().get("AuthToken").get(0));


                // 여기에 security context 인증 정보 넣어야 할지도 .
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                filterChain.doFilter(request, response);
            }
            return;
        }
        catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        catch (HttpClientErrorException e){

            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            // 응답 헤더에 Content-Type 설정
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            response.setCharacterEncoding("UTF-8");

            // 에러 메시지를 응답 본문에 작성
            response.getWriter().write(ErrorType.AUTH_SERVER_ERROR.getMessage());
            return;
        } catch(CuriException e){


            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());
            filterChain.doFilter(request, response);

            return;
        }
    }

    private ResponseEntity communicateWithAuthServer(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpMethod httpMethod = HttpMethod.GET; // 호출할 HTTP 메서드 선택 (GET, POST, 등)
        URI requestUri = URI.create(constants.getAUTH_SERVER().concat("/verify"));
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

    private void pretendTobeAuthorized (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("AuthToken",  "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJmbG9OM1BZanhiUTlFM01RSm1pSGh3RHhCd2IyIiwiaWF0IjoxNjkwMTg2NDgxLCJleHAiOjE4MTAxODY0ODF9.rUrshoegZWhHyo1m6xQQyrzn7pzuCgDG1TQ_9BpOi2s");

        // Parse the responseBody JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        // Extract userId
        String userId = "8514199@gmail.com";
        String userName = "jiseung";
        //String userEmail = jsonNode.get("userEmail").asText();

        userService.dbStore(userId, userName);


        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        currentUser.setName(userName);
        //currentUser.setUserEmail(getUserEmail(userId));
        currentUser.setNewAuthToken( "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJmbG9OM1BZanhiUTlFM01RSm1pSGh3RHhCd2IyIiwiaWF0IjoxNjkwMTg2NDgxLCJleHAiOjE4MTAxODY0ODF9.rUrshoegZWhHyo1m6xQQyrzn7pzuCgDG1TQ_9BpOi2s");


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);

    }




}

