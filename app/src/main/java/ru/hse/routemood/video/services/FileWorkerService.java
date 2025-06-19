package ru.hse.routemood.video.services;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.video.dao.FileStorageDao;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.utils.FileStorageUtil;

@Service
@Log4j2
public class FileWorkerService {

    @Autowired
    private FileStorageDao dao;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    public UploadMedia saveMedia(MultipartFile file, String username) throws IOException {
        return saveMedia(file, username, "https://1xbet.com");
    }

    public UploadMedia saveMedia(MultipartFile file, String username, String url) throws IOException {
        UploadMedia media = UploadMedia.builder()
            .originalPath(fileStorageUtil.createFile(file))
            .username(username)
            .url(url)
            .build();
        log.info(media);

        return dao.upload(media);
    }

    public Optional<UploadMedia> random() throws IOException {
        String path = fileStorageUtil.randomFile();
        log.info(path);
        var result = dao.random(path);

        while (result.isEmpty()) {
            fileStorageUtil.deleteFile(path);
            result = dao.random(path);
        }

        return result;
    }
    public Optional<UploadMedia> findById(UUID id) {
        return dao.find(id);
    }

    public byte[] getFile(String location) throws IOException {
        return fileStorageUtil.getFile(location);
    }

    public void deleteFile(String path) throws IOException {
        fileStorageUtil.deleteFile(path);
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
