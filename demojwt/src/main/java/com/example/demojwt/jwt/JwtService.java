package com.example.demojwt.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.boot.autoconfigure.ssl.SslBundleProperties.Key;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demojwt.Users.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";

    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClains, UserDetails user){
        try {
            return Jwts
                .builder()
                .claims(extraClains)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getKey())
                .compact();
        } catch (Exception e) {
            // Manejar la excepción o imprimir información de depuración
            e.printStackTrace();
            throw new RuntimeException("Error al generar el token JWT", e);
        }
    }

    private SecretKey getKey() {
        byte[] keyBytes=Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&& !istokenExpired(token));
    }

    private Claims getAllClaims(String token){
        return Jwts
            .parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public <T> T getClaims(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token)
    {
        return getClaims(token,Claims::getExpiration);
    }

    private boolean istokenExpired(String token)
    {
        return getExpiration(token).before(new Date());
    }
}
