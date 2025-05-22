package ru.hse.routemood.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.hse.routemood.image.models.Image;

@DataJpaTest
class ImageServiceRepositoryTest {

    @Autowired
    private ImageServiceRepository repository;

    private Image savedImage;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        savedImage = repository.save(
            Image.builder()
                .fileName("landscape.jpg")
                .filePath("/images/landscape.jpg")
                .mimeType("image/jpg")
                .build()
        );
    }

    @Test
    void testSave() {
        Image newImage = Image.builder()
            .fileName("portrait.jpg")
            .filePath("/uploads/portrait.jpg")
            .mimeType("image/jpg")
            .build();

        Image result = repository.save(newImage);

        assertNotNull(result.getId());
        assertEquals(newImage.getFileName(), result.getFileName());
        assertEquals(newImage.getMimeType(), result.getMimeType());
    }

    @Test
    void testFindById_WithExistingId() {
        Optional<Image> result = repository.findById(savedImage.getId());

        assertTrue(result.isPresent());
        assertEquals(savedImage.getId(), result.get().getId());
        assertEquals(savedImage.getFileName(), result.get().getFileName());
    }

    @Test
    void testFindById_WithNonExistingId() {
        Optional<Image> result = repository.findById(UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        repository.save(
            Image.builder()
                .fileName("nature.jpg")
                .filePath("/gallery/nature.jpg")
                .mimeType("image/jpg")
                .build()
        );

        List<Image> images = repository.findAll();

        assertEquals(2, images.size());
        assertTrue(images.stream().anyMatch(img -> img.getFileName().equals("landscape.jpg")));
    }

    @Test
    void testDelete() {
        repository.delete(savedImage);

        Optional<Image> result = repository.findById(savedImage.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdate() {
        savedImage.setFileName("updated.jpg");
        repository.save(savedImage);

        Optional<Image> result = repository.findById(savedImage.getId());
        assertTrue(result.isPresent());
        assertEquals("updated.jpg", result.get().getFileName());
    }

    @Test
    void testSaveAllAndFindAll() {
        List<Image> images = Arrays.asList(
            Image.builder().fileName("a.jpg").filePath("/a.jpg").mimeType("image/jpg").build(),
            Image.builder().fileName("b.jpg").filePath("/b.jpg").mimeType("image/jpg").build()
        );

        repository.saveAll(images);
        List<Image> results = repository.findAll();

        assertEquals(3, results.size());
    }
}