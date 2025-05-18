package ru.hse.routemood.image;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.dto.ImageLoadResponse;
import ru.hse.routemood.image.dto.ImageSaveResponse;
import ru.hse.routemood.image.models.Image;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageServiceRepository imageServiceRepository;
    @Value("${image.storage.path}")
    private String storagePath;

    private String saveResource(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    private Resource loadAsResource(String filePath) throws MalformedURLException {
        Path path = Paths.get(filePath);
        return new UrlResource(path.toUri());
    }

    private Image findById(@NonNull UUID id) {
        return imageServiceRepository.findById(id).orElse(null);
    }

    public ImageSaveResponse save(@NonNull MultipartFile file, @NonNull String mimeType) {
        String fileName = ImageNameGenerator.generateFileName(file);

        System.out.println("Generated image name: " + fileName);

        String filePath;
        try {
            filePath = saveResource(file, fileName);
        } catch (IOException e) {
            // TODO more useful action
            return null;
        }
        Image image = Image.builder()
            .fileName(fileName)
            .filePath(filePath)
            .mimeType(mimeType)
            .build();
        return new ImageSaveResponse(imageServiceRepository.save(image));
    }

    public ImageLoadResponse load(@NonNull UUID imageId) {
        Image image = findById(imageId);
        if (image == null) {
            return null;
        }
        Resource file;
        try {
            file = loadAsResource(image.getFilePath());
        } catch (IOException e) {
            // TODO more useful action
            return null;
        }
        return ImageLoadResponse.builder()
            .file(file)
            .mimeType(image.getMimeType())
            .build();
    }

    public boolean delete(@NonNull UUID id) {
        if (imageServiceRepository.existsById(id)) {
            imageServiceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
