package ru.hse.routemood.video.dao;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.repository.FileRepository;

@Service
public class FileStorageDao {
    @Autowired
    private FileRepository repository;

    public UploadMedia upload(UploadMedia media) {
        return repository.save(media);
    }

    public Optional<UploadMedia> find(UUID id) {
        return repository.findById(id);
    }
    public Optional<UploadMedia> random(String path) {
        return repository.findByOriginalPath(path);
    }


}
