package ru.hse.routemood.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.dto.ImageLoadResponse;
import ru.hse.routemood.image.dto.ImageSaveResponse;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private UUID testId;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test data".getBytes()
        );
    }

    @Test
    void saveImage_Success() {
        ImageSaveResponse mockResponse = new ImageSaveResponse(testId);
        when(imageService.save(any(MultipartFile.class))).thenReturn(mockResponse);

        ResponseEntity<ImageSaveResponse> response = imageController.saveImage(testFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(imageService).save(testFile);
    }

    @Test
    void saveImage_ServiceReturnsNull_InternalServerError() {
        when(imageService.save(any())).thenReturn(null);

        ResponseEntity<ImageSaveResponse> response = imageController.saveImage(testFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void loadImage_Success() throws Exception {
        ImageLoadResponse mockResponse = new ImageLoadResponse(
            "test".getBytes(),
            "image/jpeg"
        );
        when(imageService.load(testId)).thenReturn(mockResponse);

        ResponseEntity<ImageLoadResponse> response = imageController.loadImage(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(imageService).load(testId);
    }

    @Test
    void loadImage_NotFound() throws Exception {
        when(imageService.load(testId)).thenReturn(null);

        ResponseEntity<ImageLoadResponse> response = imageController.loadImage(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void loadImage_ServiceThrowsException_InternalServerError() throws Exception {
        when(imageService.load(testId)).thenThrow(new IOException("Test exception"));

        ResponseEntity<ImageLoadResponse> response = imageController.loadImage(testId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteImage_Success() throws Exception {
        when(imageService.delete(testId)).thenReturn(true);

        ResponseEntity<Void> response = imageController.deleteImage(testId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(imageService).delete(testId);
    }

    @Test
    void deleteImage_NotFound() throws Exception {
        when(imageService.delete(testId)).thenReturn(false);

        ResponseEntity<Void> response = imageController.deleteImage(testId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteImage_ServiceThrowsException_InternalServerError() throws Exception {
        when(imageService.delete(testId)).thenThrow(new IOException("Test exception"));

        ResponseEntity<Void> response = imageController.deleteImage(testId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
