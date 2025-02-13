package study.demo.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import study.demo.service.exception.DataInvalidException;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;
    
    private final MessageSource messages;

    // create new access token with secret key and algorithm HS256
    public String generateTokenFromUserName(String userName) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // create new refresh token as access token with the same jti and longer expiration time
    public String generateRefreshToken(String token) {
        final Claims tokenClaims = extractAllClaims(token);
        HashMap<String, Boolean> rfclaim = new HashMap<>();
        rfclaim.put("isRefreshToken",true);
        return Jwts.builder()
                .setClaims(rfclaim)
                .setId(tokenClaims.getId())
                .setSubject(tokenClaims.getSubject())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenDurationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // get username from token 
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    //get claims from token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // create key with secret key for generating token
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    
    // check whether token is valid or not 
    public boolean isTokenValid(String token, UserDetails userDetails, String request) {
        if (isRefrehToken(token) && !request.endsWith("refreshtoken")) {
            throw new DataInvalidException(messages.getMessage("is.refreshtoken", null, Locale.getDefault()),"is.refreshtoken");

        } else if (!isRefrehToken(token) && request.endsWith("refreshtoken")) {
            throw new DataInvalidException(messages.getMessage("is.accesstoken", null, Locale.getDefault()),"is.accesstoken");
        }
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // get jti from token
    public String extractJti(String token) {
        return extractAllClaims(token).getId();
    }
    
    // check if the refresh token or not
    public boolean isRefrehToken(String token) {
        return Boolean.TRUE.equals(extractAllClaims(token).get("isRefreshToken"));
    }
    
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
    
}
