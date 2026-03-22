package com.banelethabede.clear_path_parent.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import com.banelethabede.clear_path_parent.role.RoleName;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // @Value("${jwt.secret}")
    private final String jwtSecret = "verySecretKeyThatIsAtLeast32ChaawdwfhiuwagavbivgiyuwviuyvbuiewiuvractersLong!!";
    // @Value("${jwt.expirationMs}")
    private final long jwtExpirationMs = 86400000; // 24 hours

    public String generateToken(String email, RoleName role) {
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey())
                .compact();

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }


    public boolean  validateToken(String token) throws JwtException {

        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private SecretKey getSignInKey() {
        //SignatureAlgorithm.HS256, this.secret
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}