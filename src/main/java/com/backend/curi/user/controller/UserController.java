package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.firebase.FirebaseAuthentication;
import com.backend.curi.user.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@RequiredArgsConstructor
//@RequestMapping("/user")
public class UserController {
    private final UserService userService;


    @GetMapping("/authorize")
    public ResponseEntity doAuthorization(HttpServletRequest request){
        try {
            String accessToken = request.getHeader("Authentication");
            log.info("accessToken: {}", accessToken);
            return userService.doAuthorization(accessToken);
        } catch (FirebaseAuthException e) {
            log.info("FirebaseAuthException");
            // Access Token이 유효하지 않은 경우 또는 검증에 실패한 경우 에러 처리
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);


        }
    }

    @GetMapping("/goWorkSpace")
    public ResponseEntity goWorkSpace(Authentication authentication){
        return new ResponseEntity(HttpStatus.ACCEPTED);
      //  authentication.getPrincipal();
    }



}
