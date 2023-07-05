package com.backend.curi.security.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtil {

    public static String getUserId(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userId", String.class);
    }

    public static boolean isValid(String token, String secretKey){
        try {
            if (token == null) {
                log.error("토큰이 null입니다.");
                return false;
            }
            return !Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());


        } catch (ExpiredJwtException e) {
            log.error("Expired token");
            return false; // 토큰이 만료됨
        } catch (JwtException e) {
            return false; // 토큰이 유효하지 않음
        }
    }

    public static String createJWT(String userId,String secretKey, Long expiredMs){
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
