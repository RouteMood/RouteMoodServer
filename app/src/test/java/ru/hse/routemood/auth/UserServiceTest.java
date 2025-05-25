package ru.hse.routemood.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.hse.routemood.auth.domain.models.User;
import ru.hse.routemood.auth.repository.UserServiceRepository;

import java.util.Optional;
import ru.hse.routemood.auth.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserServiceRepository repository;

    @InjectMocks
    private UserService userService;

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
}
