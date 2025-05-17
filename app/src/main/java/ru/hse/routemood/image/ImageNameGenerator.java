package ru.hse.routemood.image;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageNameGenerator {

    private static final String namePattern = "IMG_%s_%s%s";
    private static final String datetimeFormat = "yyyyMMddHHmmssSSS";

    private static String generateFileName(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return String.format(namePattern,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(datetimeFormat)),
            UUID.randomUUID().toString().replace("-", ""),
            (extension != null ? "." + extension : "")
        );
    }

    public static String generateFileName(MultipartFile file) {
        return generateFileName(file.getOriginalFilename());
    }
}
