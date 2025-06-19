package ru.hse.routemood.video.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;


@Service
public class FileStorageUtil {

    private Random random = new Random();

    public String getHomeDirectory() {
        return System.getProperty("user.home") + File.separator + "videos" + File.separator;
    }

    public String createFile(MultipartFile file) throws IOException {
        String dirPath = getHomeDirectory()
            + LocalDateTime.now();

        String fileName = String.format(
            "%s.%s",
            UUID.randomUUID(),
            FilenameUtils.getExtension(file.getOriginalFilename())
        );

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String location = dirPath + File.separator + fileName;
        file.transferTo(new File(location));
        return location;
    }

    public void deleteFile(String path) throws IOException {
        File file = new File(path);
        var directory = file.getParent();

        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        file = new File(directory);
        if (file.listFiles().length == 0) {
            FileUtils.deleteDirectory(file);
        }
    }

    public byte[] getFile(String path) throws IOException {
        File file = new File(path);
        byte[] b = null;
        if (file.exists()) {
            b = FileUtils.readFileToByteArray(file);
        }

        return b;
    }

    public String randomFile() throws IOException {
        File dir = new File(getHomeDirectory());
        File[] dirs = dir.listFiles();
        dir = dirs[random.nextInt(dirs.length)];

        dirs = dir.listFiles();
        return dirs[random.nextInt(dirs.length)].getPath();
    }
}
