package com.banelethabede.clear_path.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;


import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // @Value("${jwt.secret}")
    private final String jwtSecret = "verySecretKeyThatIsAtLeast32ChaawdwfhiuwagavbivgiyuwviuyvbuiewiuvractersLong!!";
    // @Value("${jwt.expirationMs}")
    private final long jwtExpirationMs = 86400000; // 24 hours


    private Claims claims;

    public void generateToken(String email, HttpServletResponse response){
        String jwt = Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey())
                .compact();

        Cookie cookie = new Cookie("JWT", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(cookie);
    }


    
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "JWT");
        System.out.println("Extracted JWT from cookie: " + (cookie != null ? cookie.getValue() : "No cookie found"));
        return cookie != null ? cookie.getValue() : null;
    }
    
    public void validateToken(String token) throws JwtException {

        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


        } catch(JwtException e){
            // catch null, wrong token, expired token
            throw new JwtException(e.getMessage() + "I am firing here");
        }
    }

    public void removeTokenFromCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    private SecretKey getSignInKey() {
        //SignatureAlgorithm.HS256, this.secret
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail() {
        return claims.getSubject();
    }
    
}