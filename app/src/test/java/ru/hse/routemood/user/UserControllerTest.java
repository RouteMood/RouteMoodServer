package ru.hse.routemood.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.user.domain.dto.UserResponse;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;
import ru.hse.routemood.user.services.UserService;

public class UserControllerTest {

    @Mock
    private UserServiceRepository userServiceRepository;

    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listUsers_ReturnsListOfUsers() {
        User user = new User();
        user.setUsername("testUser");
        List<User> users = List.of(user);
        when(userServiceRepository.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.listUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void listUsers_ReturnsEmptyList() {
        when(userServiceRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.listUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getUserInfo_UserExists_ReturnsUserResponse() {
        String username = "testUser";
        UserResponse expectedResponse = UserResponse.builder()
            .username(username)
            .avatarId(UUID.randomUUID())
            .build();
        when(userService.getUserInfo(username)).thenReturn(expectedResponse);
        when(userServiceRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        ResponseEntity<UserResponse> response = userController.getUserInfo(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getUserInfo_UserNotFound_ReturnsNotFound() {
        String username = "unknown";
        when(userServiceRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> response = userController.getUserInfo(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateAvatar_Success_ReturnsAvatarId() throws IOException {
        String username = "testUser";
        UUID avatarId = UUID.randomUUID();
        MultipartFile mockFile = mock(MultipartFile.class);
        String authHeader = "Bearer token";

        when(jwtService.extractUsername("token")).thenReturn(username);
        when(userService.updateAvatar(username, mockFile)).thenReturn(avatarId);

        ResponseEntity<UUID> response = userController.updateAvatar(mockFile, authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(avatarId, response.getBody());
    }

    @Test
    void updateAvatar_UserNotFound_ReturnsNotFound() throws IOException {
        String username = "unknown";
        String authHeader = "Bearer token";

        when(jwtService.extractUsername("token")).thenReturn(username);
        when(userService.updateAvatar(eq(username), any(MultipartFile.class))).thenReturn(null);

        ResponseEntity<UUID> response = userController.updateAvatar(
            mock(MultipartFile.class),
            authHeader
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateAvatar_InternalError_Returns500() throws IOException {
        String username = "testUser";
        MultipartFile mockFile = mock(MultipartFile.class);
        String authHeader = "Bearer token";

        when(jwtService.extractUsername("token")).thenReturn(username);
        when(userService.updateAvatar(username, mockFile)).thenThrow(new IOException("Error"));

        ResponseEntity<UUID> response = userController.updateAvatar(mockFile, authHeader);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void updateAvatar_NullFile_Returns500() throws IOException {
        String username = "testUser";
        String authHeader = "Bearer token";

        when(jwtService.extractUsername("token")).thenReturn(username);
        when(userService.updateAvatar(username, null)).thenThrow(IOException.class);

        ResponseEntity<UUID> response = userController.updateAvatar( null, authHeader);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
