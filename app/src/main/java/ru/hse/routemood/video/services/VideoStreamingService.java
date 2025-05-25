package ru.hse.routemood.video.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class VideoStreamingService {

    @Autowired
    private FileWorkerService fileWorkerService;
    public ResponseEntity<Resource> prepareContent(UUID id, HttpHeaders headers) throws IOException {
        File videoFile = fileWorkerService.getFile(id);

        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        long fileLength = videoFile.length();
        String rangeHeader = headers.getFirst(HttpHeaders.RANGE);

        long rangeStart = 0;
        long rangeEnd = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            try {
                rangeStart = Long.parseLong(ranges[0]);
                if (ranges.length > 1) {
                    rangeEnd = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                rangeStart = 0;
                rangeEnd = fileLength - 1;
            }
        }

        if (rangeEnd >= fileLength) {
            rangeEnd = fileLength - 1;
        }

        long contentLength = rangeEnd - rangeStart + 1;
        InputStream inputStream = new FileInputStream(videoFile);
        inputStream.skip(rangeStart);

        InputStreamResource resource = new InputStreamResource(new LimitedInputStream(inputStream, contentLength));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "video/mp4");
        responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        responseHeaders.set(
            HttpHeaders.CONTENT_RANGE,
            String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileLength)
        );

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .headers(responseHeaders)
            .contentLength(contentLength)
            .body(resource);
    }

    private static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        protected LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.remaining = limit;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) return -1;
            int result = in.read();
            if (result != -1) remaining--;
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) return -1;
            len = (int) Math.min(len, remaining);
            int result = in.read(b, off, len);
            if (result != -1) remaining -= result;
            return result;
        }
    }
}