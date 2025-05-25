package ru.hse.routemood.auth;

import com.google.api.client.util.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import ru.hse.routemood.auth.services.JwtService;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService();
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretBase64 = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(jwtService, "secretKey", secretBase64);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 1000 * 60 * 60 * 2); // 2 hours
    }

    @Test
    public void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testGenerateAccessToken() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(List.of()).build();
        String token = jwtService.generateAccessToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testExtractUsername() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(List.of()).build();
        String token = jwtService.generateAccessToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    public void testIsExpiredFalse() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(List.of()).build();
        String token = jwtService.generateAccessToken(userDetails);
        assertFalse(jwtService.isExpired(token));
    }

    @Test
    public void testIsTokenValid() {
        UserDetails userDetails = User.withUsername("testuser").password("pass").authorities(List.of()).build();
        String token = jwtService.generateAccessToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testIsTokenValidFalse_WrongUser() {
        UserDetails correctUser = User.withUsername("correct").password("pass").authorities(List.of()).build();
        UserDetails wrongUser = User.withUsername("wrong").password("pass").authorities(List.of()).build();
        String token = jwtService.generateAccessToken(correctUser);
        assertFalse(jwtService.isTokenValid(token, wrongUser));
    }
}