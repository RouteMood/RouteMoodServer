package ru.hse.routemood.rating.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.hse.routemood.rating.models.RatingItem;

public interface RatingServiceRepository extends JpaRepository<RatingItem, UUID>,
    CustomRatingServiceRepository {

    @NonNull
    @Override
    Optional<RatingItem> findById(@NonNull UUID id);

    List<RatingItem> findAllByAuthorUsername(@NonNull String authorUsername);
}
