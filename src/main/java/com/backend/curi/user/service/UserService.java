package com.backend.curi.user.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.firebase.FirebaseAuthentication;
import com.backend.curi.security.utils.JwtUtil;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000 * 60 * 60l;
    private final UserRepository userRepository;

    public ResponseEntity authorize(String accessToken) throws FirebaseAuthException{
        // Access Token 검증
        FirebaseToken decodedToken = FirebaseAuthentication.verifyAccessToken(accessToken);

        // 유효한 Access Token으로부터 사용자 정보 가져오기
        String userId = decodedToken.getUid();
        String email = decodedToken.getEmail();



        log.info("userId : {}", userId);
        log.info("email : {}", email);

        //make JWT
        String JWT = JwtUtil.createJWT(userId, secretKey, expiredMs);

        // Put JWT in header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", JWT);

        log.info("JWT : {}", JWT);

        return new ResponseEntity(headers, HttpStatus.ACCEPTED);
    }

    public void createWorkspace (String userId, int workSpaceId){
        User_ user = userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        user.setWorkSpaceId(workSpaceId);
        userRepository.save(user);
    }

    public int getWorkSpaceIdByUserId (String userId){
        return userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS)).getWorkSpaceId();

    }

    public void dbStore (String accessToken) throws FirebaseAuthException {
        // Access Token 검증
        FirebaseToken decodedToken = FirebaseAuthentication.verifyAccessToken(accessToken);

        // 유효한 Access Token으로부터 사용자 정보 가져오기
        String userId = decodedToken.getUid();
        String email = decodedToken.getEmail();

        User_ user = User_.builder().userId(userId).email(email).build();
        userRepository.save(user);

    }


}
