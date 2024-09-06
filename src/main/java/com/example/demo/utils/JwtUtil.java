package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String ISSUER = "ingesis.uniquindio.edu.co";

    public static Claims validateToken(String token) throws Exception {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!ISSUER.equals(claims.getIssuer())) {
            throw new Exception("Issuer inválido");
        }

        if (claims.getExpiration().before(new Date())) {
            throw new Exception("Token expirado");
        }

        return claims;
    }

    public static String generarToken(String usuario) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + 3600000; // 1 hora
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(usuario)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }
}
