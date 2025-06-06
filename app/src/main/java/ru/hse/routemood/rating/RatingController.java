package ru.hse.routemood.rating;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.rating.dto.PageResponse;
import ru.hse.routemood.rating.dto.RateRequest;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;
import ru.hse.routemood.rating.services.RatingService;

@RestController
@RequestMapping(path = "/rating")
@AllArgsConstructor
@Log4j2
public class RatingController {

    private final RatingService ratingService;
    private final JwtService jwtService;

    @PostMapping(path = "/save")
    public ResponseEntity<RatingResponse> saveRoute(@RequestBody RatingRequest request) {
        log.info("Get saveRoute request: {}", request);

        //TODO check existence
        RatingResponse response = ratingService.save(request);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        log.info("Save route response: {}", response);
        log.info("ResponseEntity: {}", ResponseEntity.ok(response));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<Void> deleteRating(@RequestParam(name = "id") UUID id) {
        boolean isDeleted = ratingService.delete(id);
        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping(path = "/add-rate")
    public ResponseEntity<RatingResponse> addRate(@RequestBody RateRequest request) {
        log.info("Get addRate request: id = {}; rate = {}", request.getId(), request.getRate());

        RatingResponse response = ratingService.addRate(request.getId(), request.getUsername(),
            request.getRate());
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-by-id")
    public ResponseEntity<RatingResponse> route(@RequestParam(name = "id") UUID routeId,
        @RequestHeader("Authorization") String authHeader) {
        log.info("Get route request: {}", routeId);
        RatingResponse response = ratingService.findById(routeId, getUsername(authHeader));
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-by-author")
    public ResponseEntity<List<RatingResponse>> listRoutesByAuthorUsername(
        @RequestParam(name = "author") String authorUsername,
        @RequestHeader("Authorization") String authHeader) {
        log.info("get route request: {}", authorUsername);
        List<RatingResponse> response = ratingService.findAllByAuthorUsername(authorUsername,
            getUsername(authHeader));
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-all")
    public ResponseEntity<List<RatingResponse>> listRoutes(
        @RequestHeader("Authorization") String authHeader) {
        log.info("Get listRoutes request");
        return ResponseEntity.ok(ratingService.findAll(getUsername(authHeader)));
    }

    @GetMapping("/first-page")
    public ResponseEntity<PageResponse> getFirstPage(
        @RequestHeader("Authorization") String authHeader) {
        log.info("Get first page request");
        return ResponseEntity.ok(ratingService.getFirstPage(getUsername(authHeader)));
    }

    @GetMapping("/next-page")
    public ResponseEntity<PageResponse> getNextPage(
        @RequestParam(name = "nextPageToken") String nextPageToken,
        @RequestHeader("Authorization") String authHeader) {
        log.info("Get next page request, nextPageToken: {}", nextPageToken);
        return ResponseEntity.ok(
            ratingService.getNextPage(nextPageToken, getUsername(authHeader)));
    }

    private String getUsername(String authHeader) {
        return jwtService.extractUsername(authHeader.substring(7));
    }


}