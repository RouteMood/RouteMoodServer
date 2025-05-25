package ru.hse.routemood.user.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;

@Service
@AllArgsConstructor
public class UserService {

    private final UserServiceRepository userServiceRepository;

    public User save(User user) {
        return userServiceRepository.save(user);
    }

    public User findByUsername(String username) {
        return userServiceRepository.findByUsername(username).orElse(null);
    }

    public UserDetailsService userDetailsService() {
        return this::findByUsername;
    }

    public User createUser(User user) {
        if (userServiceRepository.existsByUsername(user.getUsername())) {
            System.out.println(user.getUsername());
            return null;
        }

        if (userServiceRepository.existsByLogin(user.getLogin())) {
            System.out.println(user.getLogin());
            return null;
        }

        return save(user);
    }

    public User createOrSave(User user) {
        User result = createUser(user);
        if (result == null) {
            result = userServiceRepository.findByUsername(user.getUsername()).orElse(null);
        }

        return result;
    }
}
