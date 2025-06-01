package ru.hse.routemood.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.dto.ImageLoadResponse;
import ru.hse.routemood.image.dto.ImageSaveResponse;
import ru.hse.routemood.image.models.Image;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {

    private final ImageServiceRepository imageServiceRepository;
    @Value("${image.storage.path}")
    private String storagePath;

    private String saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    private Image findById(@NonNull UUID id) {
        return imageServiceRepository.findById(id).orElse(null);
    }

    public ImageSaveResponse save(@NonNull MultipartFile file) {
        String fileName = ImageNameGenerator.generateFileName(file);

        log.info("Generated image name: {}", fileName);

        String filePath;
        try {
            filePath = saveFile(file, fileName);
        } catch (IOException e) {
            // TODO more useful action
            return null;
        }

        Image image = Image.builder()
            .fileName(fileName)
            .filePath(filePath)
            .mimeType(file.getContentType())
            .build();
        return new ImageSaveResponse(imageServiceRepository.save(image));
    }

    public ImageLoadResponse load(@NonNull UUID imageId) throws IOException {
        Image image = findById(imageId);
        if (image == null) {
            return null;
        }

        byte[] fileData = Files.readAllBytes(Paths.get(image.getFilePath()));

        return ImageLoadResponse.builder()
            .fileData(fileData)
            .mimeType(image.getMimeType())
            .build();
    }

    public boolean delete(@NonNull UUID id) throws IOException {
        if (imageServiceRepository.existsById(id)) {
            Image image = findById(id);

            Files.deleteIfExists(Paths.get(image.getFilePath()));

            imageServiceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
