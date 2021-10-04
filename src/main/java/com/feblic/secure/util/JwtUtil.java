package com.feblic.secure.util;

import com.feblic.secure.constants.jwt.TokenType;
import com.feblic.secure.models.auth.AuthToken;
import com.feblic.secure.services.auth.AuthTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.access_token.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access_token.validity}")
    public long accessTokenValidity;

    @Value("${jwt.refresh_token.secret}")
    private String refreshTokenSecret;

    @Value("${jwt.refresh_token.validity}")
    public long refreshTokenValidity;

    @Autowired
    AuthTokenService authTokenService;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private Claims generateClaims(Long userId, TokenType tokenType) {
        Claims claims = Jwts.claims();

        claims.setId(userId.toString());
        claims.put("user_id", userId);
        claims.put("token_type", tokenType);

        return claims;
    }

    public Claims getAllClaimsFromToken(String token, TokenType tokenType) {
        String secret = accessTokenSecret;

        if(tokenType == TokenType.REFRESH_TOKEN){
            secret = refreshTokenSecret;
        }

        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String generateToken(Long userId, TokenType tokenType) {
        long validity = accessTokenValidity;
        String secret = accessTokenSecret;

        if(tokenType == TokenType.REFRESH_TOKEN){
            validity = refreshTokenValidity;
            secret = refreshTokenSecret;
        }

        return Jwts.builder()
                .setClaims(generateClaims(userId, tokenType))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, TokenType tokenType) {
        try {
            Claims claims = getAllClaimsFromToken(token, tokenType);
            Date expiration = claims.getExpiration();
            AuthToken authToken = authTokenService.findByToken(token);

            return expiration.after(new Date()) && !authToken.isDeleted();
        } catch (Exception exception) {
            logger.error("Token validation failed : " + exception.getMessage());
            return false;
        }
    }
}
