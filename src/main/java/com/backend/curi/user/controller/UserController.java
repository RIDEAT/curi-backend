package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.firebase.FirebaseAuthentication;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.security.dto.TokenDto;
import com.backend.curi.user.repository.RefreshTokenRepository;
import com.backend.curi.user.repository.entity.RefreshToken;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Value("${jwt.refreshExpiredMs}")
    private Long refreshExpiredMs;

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

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
            log.info("Cookie 에 담은 refreshToken: {}", tokenDto.getRefreshToken());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            Map<String, Object> responseBody= new HashMap<>();
            responseBody.put("userId", userId);
            responseBody.put("userEmail", userEmail);


            return new ResponseEntity(responseBody, headers, HttpStatus.ACCEPTED);
        } catch (FirebaseAuthException e) {
            log.info("FirebaseAuthException");
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", "Firebase access token이 유효하지 않습니다.");

            // Access Token이 유효하지 않은 경우 또는 검증에 실패한 경우 에러 처리
            return new ResponseEntity(errorBody, HttpStatus.NOT_ACCEPTABLE);


        } catch(CuriException e){

            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, e.getHttpStatus());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity logout(Authentication authentication){
        try {
            if (authentication == null) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(currentUser.getUserId());
            refreshTokenRepository.delete(refreshToken.get());
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("logout userId", currentUser.getUserId());
            return new ResponseEntity(responseBody, HttpStatus.ACCEPTED);
        } catch (CuriException e){
            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e){
            log.info(e.getMessage());
            // 여기 추가해야 한다.
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);

        }
    }

    // user/validate
    @GetMapping("/validate")
    public ResponseEntity validate(Authentication authentication){
        try {
            if (authentication == null) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);

            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String userId = currentUser.getUserId();
            String email = userService.getEmailByUserId(userId);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", userId);
            responseBody.put("email", email);
            responseBody.put("result", "valid");
            return new ResponseEntity(responseBody, HttpStatus.ACCEPTED);

        }
        catch (CuriException e){
            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, HttpStatus.NOT_ACCEPTABLE);

        }


    }





}
