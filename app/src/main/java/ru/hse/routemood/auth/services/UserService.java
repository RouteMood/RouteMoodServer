package ru.hse.routemood.auth.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.hse.routemood.auth.domain.models.User;
import ru.hse.routemood.auth.repository.UserServiceRepository;

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
}
