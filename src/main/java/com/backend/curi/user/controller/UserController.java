package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.firebase.FirebaseAuthentication;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/authorize")
    public ResponseEntity authorize(HttpServletRequest request){
        try {
            String accessToken = request.getHeader("Authentication");
            log.info("accessToken: {}", accessToken);
            ResponseEntity responseEntity = userService.authorize(accessToken);
            userService.dbStore(accessToken);

            return responseEntity;
        } catch (FirebaseAuthException e) {
            log.info("FirebaseAuthException");
            // Access Token이 유효하지 않은 경우 또는 검증에 실패한 경우 에러 처리
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);


        } catch(CuriException e){

            log.info(e.getMessage());
            return new ResponseEntity(e.getHttpStatus());
        }
    }

    @GetMapping("/enter")
    public ResponseEntity<Integer> enterWorkSpace(Authentication authentication){
        try {
            String userId = authentication.getPrincipal().toString();
            int workSpaceId = userService.getWorkSpaceIdByUserId(userId);

            //redirect to workspace/{workSpaceId}

            return new ResponseEntity((Integer)workSpaceId, HttpStatus.ACCEPTED);
            //  authentication.getPrincipal();
        } catch(CuriException e){

            log.info(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
    }



}
