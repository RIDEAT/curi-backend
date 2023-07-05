package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.firebase.FirebaseAuthentication;
import com.backend.curi.security.dto.TokenDto;
import com.backend.curi.security.utils.JwtUtil;
import com.backend.curi.user.repository.RefreshTokenRepository;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.RefreshToken;
import com.backend.curi.user.repository.entity.User_;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    @Value("${jwt.authSecretKey}")
    private String authSecretKey;

    @Value("${jwt.refreshSecretKey}")
    private String refreshSecretKey;

    @Value("${jwt.authExpiredMs}")
    private Long authExpiredMs;

    @Value("${jwt.refreshExpiredMs}")
    private Long refreshExpiredMs;


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto authorize(String userId) {
        log.info("userId : {}", userId);

        //make auth JWT
        String authJWT = JwtUtil.createJWT(userId, authSecretKey, authExpiredMs);

        //make refresh JWT
        String refreshJWT = JwtUtil.createJWT(userId, refreshSecretKey, refreshExpiredMs);

        log.info("userId : {} 기 만든 refreshJWT : {}", userId, refreshJWT);


        //refresh 토큰이 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(userId);


        // 있다면 새토큰 발급후 업데이트
        // 없다면 새로 만들고 디비 저장
        if (refreshToken.isPresent()){
            refreshTokenRepository.save(refreshToken.get().updateToken(refreshJWT));
        } else{
            RefreshToken newToken = new RefreshToken(refreshJWT, userId);
            refreshTokenRepository.save(newToken);
        }

        return new TokenDto(authJWT, refreshJWT);
    }



    public void createWorkspace (String userId, int workSpaceId){
        User_ user = userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        user.setWorkSpaceId(workSpaceId);
        userRepository.save(user);
    }

    public int getWorkSpaceIdByUserId (String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS)).getWorkSpaceId();

    }

    public String getEmailByUserId (String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS)).getEmail();
    }

    public void dbStore (String userId, String email) {
        // 이미 유저가 있는 경우는 빼야하나?
        User_ user = User_.builder().userId(userId).email(email).build();
        userRepository.save(user);

    }


}
