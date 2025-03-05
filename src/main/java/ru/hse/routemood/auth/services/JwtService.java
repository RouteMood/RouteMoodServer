package ru.hse.routemood.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.hse.routemood.auth.models.User;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String getUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Date extractIssue(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public boolean isExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, refreshExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, jwtExpiration);
    }

    private String generateToken(Map<String, Objects> claims, User user, long expiration) {
        return Jwts.builder().claims().add(claims).subject(user.getId().toString())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .and()
            .signWith(getSign())
            .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> function) {
        return function.apply(extractAllClaims(token));
    }

    public boolean isTokenValid(String token, UserDetails user) {
        return user.getUsername().equals(getUserId(token)) && !isExpired(token);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSign()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSign() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }
}
