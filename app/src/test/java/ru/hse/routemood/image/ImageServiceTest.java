package ru.hse.routemood.image;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.routemood.image.dto.ImageLoadResponse;
import ru.hse.routemood.image.dto.ImageSaveResponse;
import ru.hse.routemood.image.models.Image;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final String testStoragePath = "test-storage";
    @Mock
    private ImageServiceRepository imageServiceRepository;
    @InjectMocks
    private ImageService imageService;
    private MultipartFile testFile;
    private Image testImage;

    @AfterAll
    static void cleanUp() throws IOException {
        FileUtils.deleteDirectory(new File(testStoragePath));
    }

    private Object invokePrivateMethod(String methodName, Object... params)
        throws InvocationTargetException, IllegalAccessException {
        for (Method method : imageService.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                method.setAccessible(true);
                return method.invoke(imageService, params);
            }
        }
        return null;
    }

    @BeforeEach
    void setUp() throws Exception {
        imageService = new ImageService(imageServiceRepository);

        Field field = imageService.getClass().getDeclaredField("storagePath");
        field.setAccessible(true);
        field.set(imageService, "test-storage");
        field.setAccessible(false);

        testFile = new MockMultipartFile(
            "test.jpg",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        testImage = Image.builder()
            .id(UUID.randomUUID())
            .fileName("test.jpg")
            .filePath(testStoragePath + "/test.jpg")
            .mimeType("image/jpeg")
            .build();
    }

    @Test
    void save_ShouldReturnImageSaveResponse_WhenSuccessful() {
        when(imageServiceRepository.save(any(Image.class))).thenReturn(testImage);

        ImageSaveResponse response = imageService.save(testFile);

        assertNotNull(response);
        assertEquals(testImage.getId(), response.getId());
    }

    @Test
    void load_ShouldReturnImageLoadResponse_WhenImageExists() throws IOException {
        Files.createDirectories(Paths.get(testStoragePath));
        Files.write(Paths.get(testImage.getFilePath()), "test content".getBytes());

        when(imageServiceRepository.findById(testImage.getId()))
            .thenReturn(Optional.of(testImage));

        ImageLoadResponse response = imageService.load(testImage.getId());

        assertNotNull(response);
        assertArrayEquals("test content".getBytes(), response.getFileData());
        assertEquals("image/jpeg", response.getMimeType());

        Files.deleteIfExists(Paths.get(testImage.getFilePath()));
    }

    @Test
    void load_ShouldReturnNull_WhenImageNotFound() throws IOException {
        when(imageServiceRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        ImageLoadResponse response = imageService.load(UUID.randomUUID());

        assertNull(response);
    }

    @Test
    void load_ShouldThrowIOException_WhenFileCorrupted() throws IOException {
        when(imageServiceRepository.findById(testImage.getId()))
            .thenReturn(Optional.of(testImage));
        Files.deleteIfExists(Paths.get(testImage.getFilePath()));

        assertThrows(IOException.class, () -> imageService.load(testImage.getId()));
    }

    @Test
    void delete_ShouldReturnTrue_WhenSuccessful() throws IOException {
        Files.createDirectories(Paths.get(testStoragePath));
        Files.write(Paths.get(testImage.getFilePath()), "test content".getBytes());

        when(imageServiceRepository.existsById(testImage.getId())).thenReturn(true);
        when(imageServiceRepository.findById(testImage.getId()))
            .thenReturn(Optional.of(testImage));
        doNothing().when(imageServiceRepository).deleteById(testImage.getId());

        boolean result = imageService.delete(testImage.getId());

        assertTrue(result);
        assertFalse(Files.exists(Paths.get(testImage.getFilePath())));
        verify(imageServiceRepository).deleteById(testImage.getId());
    }

    @Test
    void delete_ShouldReturnFalse_WhenImageNotExist() throws IOException {
        when(imageServiceRepository.existsById(any(UUID.class))).thenReturn(false);

        boolean result = imageService.delete(UUID.randomUUID());

        assertFalse(result);
    }

    @Test
    void findById_ShouldReturnImage_WhenExists()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(imageServiceRepository.findById(testImage.getId()))
            .thenReturn(Optional.of(testImage));

        Method method = ImageService.class.getDeclaredMethod("findById", UUID.class);
        method.setAccessible(true);

        Image result = (Image) method.invoke(imageService, testImage.getId());

        assertNotNull(result);
        assertEquals(testImage.getId(), result.getId());
        verify(imageServiceRepository).findById(testImage.getId());
    }

    @Test
    void findById_ShouldReturnNull_WhenNotExists()
        throws InvocationTargetException, IllegalAccessException {
        UUID randomId = UUID.randomUUID();
        when(imageServiceRepository.findById(randomId)).thenReturn(Optional.empty());

        Image result = (Image) invokePrivateMethod("findById", randomId);

        assertNull(result);
        verify(imageServiceRepository).findById(randomId);
    }

    @Test
    void saveFile_ShouldOverwriteFile_WhenAlreadyExists()
        throws IOException, InvocationTargetException, IllegalAccessException {
        Path existingFile = Paths.get(testStoragePath, "existing.jpg");
        Files.createDirectories(existingFile.getParent());
        Files.write(existingFile, "old content".getBytes());

        MultipartFile newFile = new MockMultipartFile(
            "existing.jpg",
            "existing.jpg",
            "image/jpeg",
            "new content".getBytes()
        );

        String resultPath = (String) invokePrivateMethod("saveFile", newFile, "existing.jpg");

        assertNotNull(resultPath);
        Path path = Paths.get(resultPath);

        byte[] content = Files.readAllBytes(path);
        assertArrayEquals("new content".getBytes(), content);

        Files.deleteIfExists(path);
    }

    @Test
    void saveFile_ShouldHandleFilenameWithSpecialCharacters()
        throws IOException, InvocationTargetException, IllegalAccessException {
        String weirdFileName = "test$%@ file(1).jpg";
        MultipartFile testFile = new MockMultipartFile(
            "original.jpg",
            weirdFileName,
            "image/jpeg",
            "content".getBytes()
        );

        String resultPath = (String) invokePrivateMethod("saveFile", testFile, weirdFileName);

        assertNotNull(resultPath);
        Path path = Paths.get(resultPath);

        assertTrue(Files.exists(path));
        assertEquals(
            Paths.get(testStoragePath).resolve(weirdFileName).toString(),
            resultPath
        );

        Files.deleteIfExists(path);
    }

}
