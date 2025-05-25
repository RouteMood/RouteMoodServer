package ru.hse.routemood.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.hse.routemood.auth.domain.dto.*;
import ru.hse.routemood.auth.domain.models.Role;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.auth.services.AuthService;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.user.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess() {
        RegisterRequest request = new RegisterRequest("username", "login", "password");
        User user = User.builder().login("login").username("username").role(Role.USER).build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateRefreshToken()).thenReturn("refreshToken");
        when(userService.createUser(any())).thenReturn(user);
        when(jwtService.generateAccessToken(any())).thenReturn("accessToken");

        AuthResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getToken());
    }

    @Test
    public void testRegisterUserNullRequest() {
        assertThrows(NullPointerException.class, () -> authService.registerUser(null));
    }

    @Test
    public void testRegisterUserUserCreationFails() {
        RegisterRequest request = new RegisterRequest("login", "username", "password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateRefreshToken()).thenReturn("refreshToken");
        when(userService.createUser(any())).thenReturn(null);

        AuthResponse response = authService.registerUser(request);
        assertNull(response);
    }

    @Test
    public void testLoginUserSuccess() {
        AuthRequest request = new AuthRequest("password", "username");
        User user = new User();
        user.setToken("refreshToken");

        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mock(Authentication.class));
        when(userService.findByUsername("username")).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");

        AuthResponse response = authService.loginUser(request);
        assertNotNull(response);
        assertEquals("accessToken", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    public void testLoginUserWrongCredentials() {
        AuthRequest request = new AuthRequest("username", "wrongpassword");
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationProvider)
            .authenticate(any());

        assertThrows(BadCredentialsException.class, () -> authService.loginUser(request));
    }

    @Test
    public void testLoginUserNotFound() {
        AuthRequest request = new AuthRequest("username", "password");

        when(authenticationProvider.authenticate(any()))
            .thenReturn(mock(Authentication.class));
        when(userService.findByUsername("username")).thenReturn(null);

        AuthResponse response = authService.loginUser(request);
        assertNull(response);
    }

    @Test
    public void testRefreshTokensSuccess() {
        RefreshRequest request = new RefreshRequest("accessToken", "refreshToken", "username");
        User user = new User();
        user.setToken("refreshToken");

        when(userService.findByUsername("username")).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken()).thenReturn("newRefreshToken");

        AuthResponse response = authService.refreshTokens(request);

        assertNotNull(response);
        assertEquals("newAccessToken", response.getToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        verify(userService).save(user);
    }

    @Test
    public void testRefreshTokensInvalidToken() {
        RefreshRequest request = new RefreshRequest("accessToken", "refreshToken", "username");
        User user = new User();
        user.setToken("realToken");

        when(userService.findByUsername("username")).thenReturn(user);

        AuthResponse response = authService.refreshTokens(request);
        assertNull(response);
    }

    @Test
    public void testRefreshTokensUserNotFound() {
        RefreshRequest request = new RefreshRequest("accessToken", "refreshToken", "username");

        when(userService.findByUsername("username")).thenReturn(null);

        AuthResponse response = authService.refreshTokens(request);
        assertNull(response);
    }
}