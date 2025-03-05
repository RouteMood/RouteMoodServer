package ru.hse.routemood.auth.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.routemood.auth.models.User;

public interface UserServiceRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
