package ru.hse.routemood.rating.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.hse.routemood.rating.models.RatingItem;

public interface RatingServiceRepository extends JpaRepository<RatingItem, UUID> {

    @NonNull
    @Override
    Optional<RatingItem> findById(@NonNull UUID id);

    List<RatingItem> findAllByAuthorUsername(@NonNull String authorUsername);

    @Query(value = "SELECT * FROM rating_item ORDER BY rating DESC, id DESC LIMIT :pageSize", nativeQuery = true)
    List<RatingItem> getFirstPage(@Param("pageSize") int pageSize);

    @Query(value = """
        SELECT * FROM rating_item 
        WHERE (rating <= :lastRating) AND (rating < :lastRating OR id < :lastId)
        ORDER BY rating DESC, id DESC 
        LIMIT :pageSize
        """, nativeQuery = true)
    List<RatingItem> getNextPage(
        @Param("lastRating") double lastRating,
        @Param("lastId") UUID lastId,
        @Param("pageSize") int pageSize
    );
}
