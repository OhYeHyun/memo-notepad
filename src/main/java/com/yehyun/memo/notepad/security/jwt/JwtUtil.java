package com.yehyun.memo.notepad.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Long ACCESS_TOKEN_EXPIRED_MS = Duration.ofMinutes(10).toMillis();
    private static final Long REFRESH_TOKEN_EXPIRED_MS = Duration.ofDays(3).toMillis();

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createAccessToken(String name, String loginId, String role) {
        return Jwts.builder()
                .claim("name", name)
                .claim("loginId", loginId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_MS))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String name) {
        return Jwts.builder()
                .claim("name", name)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_MS))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isExpiredToken(String token) {
        Claims claims = parseClaims(token);
        return claims == null || claims.getExpiration().before(new Date());
    }

    public Duration getAccessTokenExpiry() {
        return Duration.ofMillis(ACCESS_TOKEN_EXPIRED_MS);
    }

    public Duration getRefreshTokenExpiry() {
        return Duration.ofMillis(REFRESH_TOKEN_EXPIRED_MS);
    }

    public String getName(String token) {
        Claims claims = parseClaims(token);
        return claims != null ? claims.get("name", String.class) : null;
    }

    public String getLoginId(String token) {
        Claims claims = parseClaims(token);
        return claims != null ? claims.get("loginId", String.class) : null;
    }

    public String getRole(String token) {
        Claims claims = parseClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }
}
