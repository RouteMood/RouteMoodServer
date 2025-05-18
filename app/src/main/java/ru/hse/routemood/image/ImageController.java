package ru.hse.routemood.image;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.dto.ImageLoadResponse;
import ru.hse.routemood.image.dto.ImageSaveResponse;

@RestController
@RequestMapping(path = "/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(path = "/save")
    public ResponseEntity<ImageSaveResponse> saveImage(@RequestPart("file") MultipartFile file,
        @RequestPart("mimeType") String mimeType) {
        System.out.println("Get saveImage request: mimeType = " + mimeType);

        ImageSaveResponse response = imageService.save(file, mimeType);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/load")
    public ResponseEntity<ImageLoadResponse> loadImage(@RequestParam(name = "id") UUID imageId) {
        System.out.println("Get loadImage request: " + imageId);
        ImageLoadResponse response = imageService.load(imageId);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Void> deleteRating(@RequestParam(name = "id") UUID id) {
        boolean isDeleted = imageService.delete(id);
        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
