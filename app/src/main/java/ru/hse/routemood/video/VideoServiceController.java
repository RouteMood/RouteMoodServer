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
import lombok.extern.log4j.Log4j2;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.video.dao.FileStorageDao;
import ru.hse.routemood.video.models.UploadMedia;
import ru.hse.routemood.video.services.FileWorkerService;
import ru.hse.routemood.video.services.VideoStreamingService;


@RestController
@RequestMapping("/api/ads")
@Log4j2
public class VideoServiceController {

    @Value("${server.types}")
    private List<String> contentTypes;

    @Autowired
    private FileWorkerService fileWorkerService;

    @Autowired
    private FileStorageDao dao;

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
        @RequestHeader("Authorization") String jwtToken,
        @RequestParam(defaultValue = "https://1xbet.com") String url) {
        log.info(file.getContentType());
        String before = "Bearer:";
        jwtToken = jwtToken.substring(before.length());
        log.info(jwtToken);
        String username = jwtService.extractUsername(jwtToken);
        log.info(username);
        String mediaType = file.getContentType();

        if (!contentTypes.contains(mediaType)) {
            return badRequest().body("GGWP");
        }

        try {
            return ok(fileWorkerService.saveMedia(file, username, url));
        } catch (IOException e) {
            return badRequest().body("biba");
        }
    }

    @GetMapping("/download/{id}.mp4")
    public ResponseEntity<?> downloadVideo(
        @PathVariable("id") UUID id,
        @RequestHeader HttpHeaders headers) throws IOException {
        var media = dao.find(id);
        if (media.isEmpty()) {
            return badRequest().body("Unknow media id");
        }
        return videoStreamingService.prepareContent(media.get(), headers);
    }

    @GetMapping("/download/random.mp4")
    public ResponseEntity<?> downloadRandom(
        @RequestHeader HttpHeaders headers) throws IOException {
        Optional<UploadMedia> mediaOptional = fileWorkerService.random();

        if (mediaOptional.isEmpty()) {
            return noContent().build();
        }

        UploadMedia media = mediaOptional.get();
        return videoStreamingService.prepareContent(media, headers);
    }
}
