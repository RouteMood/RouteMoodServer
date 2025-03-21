package ru.hse.routemood.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.hse.routemood.auth.domain.models.User;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
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

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            claims.put("login", user.getLogin());
        }

        return generateToken(claims, userDetails, refreshExpiration);
    }


    private String generateToken(Map<String, Object> claims, UserDetails user, long expiration) {
        return Jwts.builder().claims().add(claims).subject(user.getUsername())
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
        return user.getUsername().equals(extractUsername(token)) && !isExpired(token);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSign()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSign() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }
}
