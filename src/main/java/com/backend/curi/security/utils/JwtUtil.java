package com.backend.curi.security.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtil {

    public static boolean isExpired(String token, String secretKey){
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());

            // 토큰의 유효성을 추가적으로 확인하는 로직을 구현할 수 있습니다.
            // 예를 들어, 클레임의 특정 필드 값을 검사하거나 추가적인 검증을 수행할 수 있습니다.
            // 필요에 따라 로직을 수정하셔서 사용하시면 됩니다.
        } catch (ExpiredJwtException e) {
            log.error("Expired token");
            return true; // 토큰이 만료됨
        } catch (JwtException e) {
            log.error("not valid token");

            return true; // 토큰이 유효하지 않음
        }
    }
    public static String createJWT(String userId, String secretKey, Long expiredMs){
        Claims claims = Jwts.claims();

        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
