package com.ktb.community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final Long expireTime;

    public JWTUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-time}") Long expireTime) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        this.expireTime = expireTime;
    }

    public Long getUserId(String token) {
        return getPayloadByToken(token).get("userId", Long.class);
    }

    public String getEmail(String token) {
        return getPayloadByToken(token).get("email", String.class);
    }

    public String getRole(String token) {
        return getPayloadByToken(token).get("role", String.class);
    }

    public boolean isExpired(String token) {
        try {
            return getPayloadByToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims getPayloadByToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public String createToken(String email, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }
}
