package ru.hse.routemood.video.services;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.video.dao.FileStorageDao;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.utils.FileStorageUtil;

@Service
public class FileWorkerService {

    @Autowired
    private FileStorageDao dao;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    public UploadMedia saveMedia(MultipartFile file, String username) throws IOException {
        UploadMedia media = UploadMedia.builder()
            .originalPath(fileStorageUtil.createFile(file))
            .username(username)
            .build();
        System.out.println(media);

        return dao.upload(media);
    }

    public Optional<UploadMedia> random() throws IOException {
        String path = fileStorageUtil.randomFile();
        System.out.println(path);
        return dao.random(path);
    }
    public Optional<UploadMedia> findById(UUID id) {
        return dao.find(id);
    }

    public byte[] getFile(String location) throws IOException {
        return fileStorageUtil.getFile(location);
    }

    public File getFile(UUID id) {
        Optional<UploadMedia> locationOptional = dao.find(id);
        if (locationOptional.isEmpty()) {
            return null;
        }

        String location = locationOptional.get().getOriginalPath();
        return new File(location);
    }
}
