package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.user.service.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.backend.curi.security.configuration.Constants.AUTH_SERVER;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 회원가입 하고 보내야함 . 유저 디비에 등록
    @PostMapping
    public ResponseEntity register(HttpServletRequest request, HttpServletResponse response){
        try {
            ResponseEntity<String> responseEntity = communicateWithAuthServer(request);
            String responseBody = responseEntity.getBody();
            log.info(responseBody);

            // Parse the responseBody JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

// Extract userEmail and userId
            String userEmail = jsonNode.get("userEmail").asText();
            String userId = jsonNode.get("userId").asText();

// Use userEmail and userId as needed
            log.info("User Email: {}", userEmail);
            log.info("User ID: {}", userId);
            // 이후에 response body 에서 userId, email 꺼내서 저장해야함.

            userService.dbStore(userId, userEmail);

            
Map<String, Object> responseBodyMap= new HashMap<>();
            responseBodyMap.put("userId", userId);
            responseBodyMap.put("userEmail", userEmail);

            return new ResponseEntity(responseBodyMap, responseEntity.getHeaders(), HttpStatus.ACCEPTED);

        } catch(CuriException e){

            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, e.getHttpStatus());
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e){
            log.info(e.getMessage());
            throw new CuriException(e.getStatusCode(), ErrorType.AUTH_SERVER_ERROR);
        }
    }


    private ResponseEntity communicateWithAuthServer(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpMethod httpMethod = HttpMethod.GET; // 호출할 HTTP 메서드 선택 (GET, POST, 등)
        URI requestUri = URI.create(AUTH_SERVER.concat("/auth/authorize"));
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



}
