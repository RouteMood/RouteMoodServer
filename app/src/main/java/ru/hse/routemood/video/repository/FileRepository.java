package ru.hse.routemood.video.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hse.routemood.video.models.UploadMedia;

public interface FileRepository extends JpaRepository<UploadMedia, UUID> {
    Optional<UploadMedia> findById(UUID id);
    Optional<UploadMedia> findByOriginalPath(String path);

}
