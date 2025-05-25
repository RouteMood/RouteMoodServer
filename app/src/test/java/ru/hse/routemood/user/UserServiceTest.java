package ru.hse.routemood.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.ImageService;
import ru.hse.routemood.image.dto.ImageSaveResponse;
import ru.hse.routemood.user.domain.dto.UserResponse;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;
import ru.hse.routemood.user.services.UserService;

public class UserServiceTest {

    @Mock
    private UserServiceRepository repository;

    @InjectMocks
    private UserService userService;

    @Mock
    private ImageService imageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave() {
        User user = new User();
        when(repository.save(user)).thenReturn(user);

        User result = userService.save(user);
        assertEquals(user, result);
    }

    @Test
    public void testFindByUsernameFound() {
        User user = new User();
        when(repository.findByUsername("user")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("user");
        assertEquals(user, result);
    }

    @Test
    public void testFindByUsernameNotFound() {
        when(repository.findByUsername("missing")).thenReturn(Optional.empty());

        User result = userService.findByUsername("missing");
        assertNull(result);
    }

    @Test
    public void testCreateUserSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setLogin("login");

        when(repository.existsByUsername("test")).thenReturn(false);
        when(repository.existsByLogin("login")).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        User result = userService.createUser(user);
        assertEquals(user, result);
    }

    @Test
    public void testCreateUserUsernameExists() {
        User user = new User();
        user.setUsername("test");

        when(repository.existsByUsername("test")).thenReturn(true);

        User result = userService.createUser(user);
        assertNull(result);
    }

    @Test
    public void testCreateUserLoginExists() {
        User user = new User();
        user.setUsername("test");
        user.setLogin("login");

        when(repository.existsByUsername("test")).thenReturn(false);
        when(repository.existsByLogin("login")).thenReturn(true);

        User result = userService.createUser(user);
        assertNull(result);
    }

    @Test
    public void testCreateOrSaveNewUser() {
        User user = new User();
        user.setUsername("new");
        user.setLogin("login");

        when(repository.existsByUsername("new")).thenReturn(false);
        when(repository.existsByLogin("login")).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        User result = userService.createOrSave(user);
        assertEquals(user, result);
    }

    @Test
    public void testCreateOrSaveExistingUser() {
        User user = new User();
        user.setUsername("existing");

        when(repository.existsByUsername("existing")).thenReturn(true);
        when(repository.findByUsername("existing")).thenReturn(Optional.of(user));

        User result = userService.createOrSave(user);
        assertEquals(user, result);
    }

    @Test
    public void testCreateOrSaveUserNotFound() {
        User user = new User();
        user.setUsername("notfound");

        when(repository.existsByUsername("notfound")).thenReturn(true);
        when(repository.findByUsername("notfound")).thenReturn(Optional.empty());

        User result = userService.createOrSave(user);
        assertNull(result);
    }

    @Test
    public void testUserDetailsService() {
        User user = new User();
        when(repository.findByUsername("name")).thenReturn(Optional.of(user));

        assertEquals(user, userService.userDetailsService().loadUserByUsername("name"));
    }

    @Test
    public void testUserDetailsServiceReturnsNull() {
        when(repository.findByUsername("none")).thenReturn(Optional.empty());
        assertNull(userService.userDetailsService().loadUserByUsername("none"));
    }

    @Test
    public void testGetUserInfoSuccess() {
        User user = new User();
        user.setUsername("testUser");
        user.setAvatarId(UUID.randomUUID());

        when(repository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserInfo("testUser");

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getAvatarId(), response.getAvatarId());
    }

    @Test
    public void testGetUserInfoUserNotFound() {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserResponse response = userService.getUserInfo("unknown");

        assertNull(response);
    }

    @Test
    public void testUpdateAvatarSuccess() throws IOException {
        User user = new User();
        user.setUsername("testUser");
        user.setAvatarId(null);

        MultipartFile mockFile = mock(MultipartFile.class);
        UUID newAvatarId = UUID.randomUUID();
        ImageSaveResponse imageResponse = new ImageSaveResponse(newAvatarId);

        when(repository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(imageService.save(any())).thenReturn(imageResponse);

        UUID result = userService.updateAvatar("testUser", mockFile);

        assertEquals(newAvatarId, result);
        verify(repository).save(user);
        assertNotNull(user.getAvatarId());
    }

    @Test
    public void testUpdateAvatarDeleteOldImage() throws IOException {
        UUID oldAvatarId = UUID.randomUUID();
        User user = new User();
        user.setUsername("testUser");
        user.setAvatarId(oldAvatarId);

        MultipartFile mockFile = mock(MultipartFile.class);
        UUID newAvatarId = UUID.randomUUID();
        ImageSaveResponse imageResponse = new ImageSaveResponse(newAvatarId);

        when(repository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(imageService.save(any())).thenReturn(imageResponse);

        userService.updateAvatar("testUser", mockFile);

        verify(imageService).delete(oldAvatarId);
    }

    @Test
    public void testUpdateAvatarUserNotFound() throws IOException {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        UUID result = userService.updateAvatar("unknown", mock(MultipartFile.class));

        assertNull(result);
    }

    @Test
    public void testUpdateAvatarImageSaveFailure() throws IOException {
        User user = new User();
        user.setUsername("testUser");

        when(repository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(imageService.save(any())).thenReturn(null);

        UUID result = userService.updateAvatar("testUser", mock(MultipartFile.class));

        assertNull(result);
        assertNull(user.getAvatarId());
    }

    @Test
    public void testCreateUserWithNullUser() {
        assertThrows(NullPointerException.class, () ->
            userService.createUser(null)
        );
    }

    @Test
    public void testCreateOrSaveWithNullUser() {
        assertThrows(NullPointerException.class, () ->
            userService.createOrSave(null)
        );
    }
}
