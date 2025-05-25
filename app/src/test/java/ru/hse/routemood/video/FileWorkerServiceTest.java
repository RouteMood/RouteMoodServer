package ru.hse.routemood.video;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.hse.routemood.video.dao.FileStorageDao;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.services.FileWorkerService;
import ru.hse.routemood.video.utils.FileStorageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileWorkerServiceTest {

    @Mock
    private FileStorageDao dao;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @InjectMocks
    private FileWorkerService fileWorkerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMedia() throws IOException {
        MockMultipartFile file = new MockMultipartFile("video", "video.mp4", "video/mp4", "data".getBytes());
        String path = "some/path/video.mp4";
        UploadMedia media = UploadMedia.builder().originalPath(path).username("user").build();

        when(fileStorageUtil.createFile(file)).thenReturn(path);
        when(dao.upload(any(UploadMedia.class))).thenReturn(media);

        UploadMedia result = fileWorkerService.saveMedia(file, "user");

        assertNotNull(result);
        assertEquals("user", result.getUsername());
        assertEquals(path, result.getOriginalPath());
    }

    @Test
    void testRandom() throws IOException {
        String path = "some/random/file.mp4";
        UploadMedia media = UploadMedia.builder().originalPath(path).build();

        when(fileStorageUtil.randomFile()).thenReturn(path);
        when(dao.random(path)).thenReturn(Optional.of(media));

        Optional<UploadMedia> result = fileWorkerService.random();

        assertTrue(result.isPresent());
        assertEquals(path, result.get().getOriginalPath());
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        UploadMedia media = UploadMedia.builder().username("test").build();

        when(dao.find(id)).thenReturn(Optional.of(media));

        Optional<UploadMedia> result = fileWorkerService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("test", result.get().getUsername());
    }

    @Test
    void testGetFileBytes() throws IOException {
        String path = "some/file.mp4";
        byte[] content = "bytes".getBytes(StandardCharsets.UTF_8);

        when(fileStorageUtil.getFile(path)).thenReturn(content);

        byte[] result = fileWorkerService.getFile(path);

        assertArrayEquals(content, result);
    }

    @Test
    void testGetFileById_Success() {
        UUID id = UUID.randomUUID();
        String path = "video/file/path.mp4";
        UploadMedia media = UploadMedia.builder().originalPath(path).build();

        when(dao.find(id)).thenReturn(Optional.of(media));

        File result = fileWorkerService.getFile(id);

        assertNotNull(result);
        assertEquals(path, result.getPath());
    }

    @Test
    void testGetFileById_NotFound() {
        UUID id = UUID.randomUUID();
        when(dao.find(id)).thenReturn(Optional.empty());

        File result = fileWorkerService.getFile(id);
        assertNull(result);
    }
}
