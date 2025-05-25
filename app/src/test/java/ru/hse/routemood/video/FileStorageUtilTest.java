package ru.hse.routemood.video;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import ru.hse.routemood.video.utils.FileStorageUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageUtilTest {

    private FileStorageUtil fileStorageUtil;

    @BeforeEach
    void setUp() {
        fileStorageUtil = new FileStorageUtil();
    }

    @Test
    void testGetHomeDirectory() {
        String expected = System.getProperty("user.home") + File.separator + "videos" + File.separator;
        assertEquals(expected, fileStorageUtil.getHomeDirectory());
    }

    @Test
    void testCreateFile() throws IOException {
        String content = "Sample content";
        MultipartFile mockFile = new MockMultipartFile(
            "test.txt",
            "test.txt",
            "text/plain",
            content.getBytes(StandardCharsets.UTF_8)
        );

        String filePath = fileStorageUtil.createFile(mockFile);
        assertNotNull(filePath);

        File savedFile = new File(filePath);
        assertTrue(savedFile.exists());
        assertEquals(content, FileUtils.readFileToString(savedFile, StandardCharsets.UTF_8));

        // Cleanup
        File parent = savedFile.getParentFile();
        FileUtils.deleteDirectory(parent);
    }

    @Test
    void testGetFileExists() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        FileUtils.writeStringToFile(tempFile, "data", StandardCharsets.UTF_8);

        byte[] result = fileStorageUtil.getFile(tempFile.getAbsolutePath());
        assertNotNull(result);
        assertEquals("data", new String(result, StandardCharsets.UTF_8));

        tempFile.delete();
    }

    @Test
    void testGetFileNotExists() throws IOException {
        byte[] result = fileStorageUtil.getFile("non_existent_file.txt");
        assertNull(result);
    }
}
