package com.iCritic.users.entrypoint.fixture;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.util.UUID;

public class JwtTokenFixture {

    public static String SECRET = "secret";
    public static String REFRESH_SECRET = "refreshSecret";
    public static int EXPIRATION = 20000;
    public static int REFRESH_EXPIRATION = 200000;

    public static String load() {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("userId", 1L)
                .claim("role", "DEFAULT")
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static String loadRefreshToken() {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("userId", 1L)
                .signWith(SignatureAlgorithm.HS512, REFRESH_SECRET)
                .compact();
    }
}
