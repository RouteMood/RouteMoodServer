package ru.hse.routemood.auth.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.routemood.auth.domain.models.User;

public interface UserServiceRepository extends JpaRepository<User, UUID> {

    boolean existsByLogin(String login);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
