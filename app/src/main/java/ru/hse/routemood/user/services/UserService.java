package ru.hse.routemood.user.services;

import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.ImageService;
import ru.hse.routemood.image.dto.ImageSaveResponse;
import ru.hse.routemood.user.domain.dto.UserResponse;
import ru.hse.routemood.user.domain.models.User;
import ru.hse.routemood.user.repository.UserServiceRepository;

@Service
@AllArgsConstructor
public class UserService {

    private final UserServiceRepository userServiceRepository;
    private final ImageService imageService;

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

    public UserResponse getUserInfo(String username) {
        User user = userServiceRepository.findByUsername(username).orElse(null);
        return user == null ? null : UserResponse.builder()
            .username(user.getUsername())
            .avatarId(user.getAvatarId())
            .build();
    }

    public UUID updateAvatar(String username, MultipartFile avatarFile) throws IOException {
        User user = userServiceRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return null;
        }

        if (user.getAvatarId() != null) {
            imageService.delete(user.getAvatarId());
        }

        ImageSaveResponse imageSaveResponse = imageService.save(avatarFile);
        if (imageSaveResponse == null) {
            return null;
        }

        UUID avatarId = imageSaveResponse.getId();
        user.setAvatarId(avatarId);
        userServiceRepository.save(user);

        return avatarId;
    }
}
