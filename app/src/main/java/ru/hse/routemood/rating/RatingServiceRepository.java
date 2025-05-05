package ru.hse.routemood.rating;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.routemood.rating.models.RatingItem;

public interface RatingServiceRepository extends JpaRepository<RatingItem, UUID> {

    Optional<RatingItem> findById(UUID id);

    List<RatingItem> findAllByAuthorUsername(String authorUsername);
}
