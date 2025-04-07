package com.amrit.SpringOauth2.core.service;

import com.amrit.SpringOauth2.entity.User;
import com.amrit.SpringOauth2.entity.UserToken;
import com.amrit.SpringOauth2.repository.UserTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final UserTokenRepository userTokenRepository;
    @Value("${jwt.secret}")
    private  String secretKey;

    public JwtService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 day expiration
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 21)) // 30 days expiration
                .signWith(getKey())
                .compact();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    private boolean isTokenExpired(String token){
        return !extractExpiration(token).before(new Date());
    }
    public boolean isValidAccessToken(String token, UserDetails userDetails){
        UserToken userToken = userTokenRepository.findByAccessToken(token);
        return (extractEmail(token).equals(userDetails.getUsername()) && isTokenExpired(token) && userToken != null);
    }

    public boolean isValidRefreshToken(String token, UserDetails userDetails){
        UserToken userToken = userTokenRepository.findByRefreshToken(token);
        return (extractEmail(token).equals(userDetails.getUsername()) && isTokenExpired(token) && userToken != null);
    }
}
