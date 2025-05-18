package ru.hse.routemood.video;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.services.FileWorkerService;
import ru.hse.routemood.video.services.VideoStreamingService;


@RestController
@RequestMapping("/api/ads")
public class VideoServiceController {

    @Value("${server.types}")
    private List<String> contentTypes;

    @Autowired
    private FileWorkerService fileWorkerService;

    @Autowired
    private VideoStreamingService videoStreamingService;

    @Autowired
    private JwtService jwtService;

    @PostMapping(
        value = "/upload",
        consumes = {
            MediaType.ALL_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadVideo(
        @RequestPart("content") MultipartFile file,
        @RequestHeader("Authorization") String jwtToken) {
//        String
        System.out.println(file.getContentType());
        String before = "Bearer:";
        jwtToken = jwtToken.substring(before.length());
        System.out.println(jwtToken);
        String username = jwtService.extractUsername(jwtToken);
        System.out.println(username);
        String mediaType = file.getContentType();
//        String username = "12";

        if (!contentTypes.contains(mediaType)) {
            return badRequest().body("GGWP");
        }

        try {
            return ok(fileWorkerService.saveMedia(file, username));
        } catch (IOException e) {
            return badRequest().body("biba");
        }
    }

    @GetMapping("/download/{id}.mp4")
    public ResponseEntity<?> downloadVideo(
        @PathVariable("id") UUID id,
        @RequestHeader HttpHeaders headers) throws IOException {
        return videoStreamingService.prepareContent(id, headers);
    }

    @GetMapping("/download/random.mp4")
    public ResponseEntity<?> downloadRandom(
        @RequestHeader HttpHeaders headers) throws IOException {
        Optional<UploadMedia> mediaOptional = fileWorkerService.random();
        if (mediaOptional.isEmpty()) {
            return noContent().build();
        }

        UploadMedia media = mediaOptional.get();
        return videoStreamingService.prepareContent(media.getId(), headers);
    }
}
