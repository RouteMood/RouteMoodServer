package ru.hse.routemood.video;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.services.FileWorkerService;
import ru.hse.routemood.video.services.VideoStreamingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoStreamingServiceTest {

    @Mock
    private FileWorkerService fileWorkerService;

    @InjectMocks
    private VideoStreamingService videoStreamingService;

    private File tempFile;

    @BeforeEach
    void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        tempFile = File.createTempFile("video", ".mp4");
        Files.write(tempFile.toPath(), "TestVideoData".getBytes());
    }

    @Test
    void testPrepareContent_FileNotFound() throws IOException {
        UUID id = UUID.randomUUID();
        when(fileWorkerService.getFile(id)).thenReturn(new File("non_existing_file.mp4"));

        UploadMedia media = UploadMedia.builder().url("12").id(id).build();
        ResponseEntity<Resource> response = videoStreamingService.prepareContent(media, new HttpHeaders());

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testPrepareContent_FullContent() throws IOException {
        UUID id = UUID.randomUUID();
        when(fileWorkerService.getFile(id)).thenReturn(tempFile);

        HttpHeaders headers = new HttpHeaders();
        UploadMedia media = UploadMedia.builder().url("12").id(id).build();
        ResponseEntity<Resource> response = videoStreamingService.prepareContent(media, headers);

        assertEquals(206, response.getStatusCodeValue());
        assertEquals("video/mp4", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertNotNull(response.getBody());
    }

    @Test
    void testPrepareContent_WithRange() throws IOException {
        UUID id = UUID.randomUUID();
        when(fileWorkerService.getFile(id)).thenReturn(tempFile);
        UploadMedia media = UploadMedia.builder().url("12").id(id).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RANGE, "bytes=0-4");

        ResponseEntity<Resource> response = videoStreamingService.prepareContent(media, headers);

        assertEquals(206, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE).startsWith("bytes 0-4/"));
        assertEquals(5, response.getHeaders().getContentLength());
    }

    @Test
    void testPrepareContent_RangeBeyondFileLength() throws IOException {
        UUID id = UUID.randomUUID();
        UploadMedia media = UploadMedia.builder().url("12").id(id).build();
        when(fileWorkerService.getFile(id)).thenReturn(tempFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RANGE, "bytes=0-9999");

        ResponseEntity<Resource> response = videoStreamingService.prepareContent(media, headers);

        assertEquals(206, response.getStatusCodeValue());
        long actualLength = tempFile.length();
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_RANGE).endsWith("/" + actualLength));
    }

    @Test
    void testPrepareContent_InvalidRangeFormat() throws IOException {
        UUID id = UUID.randomUUID();
        UploadMedia media = UploadMedia.builder().url("12").id(id).build();
        when(fileWorkerService.getFile(id)).thenReturn(tempFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RANGE, "invalid");

        ResponseEntity<Resource> response = videoStreamingService.prepareContent(media, headers);

        assertEquals(206, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
