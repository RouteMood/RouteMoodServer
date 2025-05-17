package ru.hse.routemood.image;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.hse.routemood.image.models.Image;

public interface ImageServiceRepository extends JpaRepository<Image, UUID> {

    @NonNull
    @Override
    Optional<Image> findById(@NonNull UUID id);
}
