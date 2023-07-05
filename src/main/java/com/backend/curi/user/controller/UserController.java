package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.firebase.FirebaseAuthentication;
import com.backend.curi.security.dto.TokenDto;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Value("${jwt.refreshExpiredMs}")
    private Long refreshExpiredMs;

    private final UserService userService;

    @GetMapping("/authorize")
    public ResponseEntity authorize(HttpServletRequest request, HttpServletResponse response){
        try {
            String accessToken = request.getHeader("Authentication");
            log.info("accessToken: {}", accessToken);
            // Access Token 검증
            FirebaseToken decodedToken = FirebaseAuthentication.verifyAccessToken(accessToken);

            // 유효한 Access Token으로부터 사용자 정보 가져오기
            String userId = decodedToken.getUid();
            String userEmail =decodedToken.getEmail();

            TokenDto tokenDto = userService.authorize(userId);

            userService.dbStore(userId, userEmail);

            // Put JWT in header
            HttpHeaders headers = new HttpHeaders();
            headers.set("AuthToken", tokenDto.getAuthToken());

            Cookie cookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
            cookie.setMaxAge(refreshExpiredMs.intValue()/1000);
            cookie.setPath("/");
            response.addCookie(cookie);

            return new ResponseEntity(headers, HttpStatus.ACCEPTED);
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
            if (workSpaceId == 0) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS);


            return new ResponseEntity((Integer)workSpaceId, HttpStatus.ACCEPTED);
            //  authentication.getPrincipal();
        } catch(CuriException e){

            log.info(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
    }



}
