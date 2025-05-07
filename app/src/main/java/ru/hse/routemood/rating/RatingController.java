package ru.hse.routemood.rating;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.rating.dto.RateRequest;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;

@RestController
@RequestMapping(path = "/rating")
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping(path = "/save")
    public ResponseEntity<RatingResponse> saveRoute(@RequestBody RatingRequest request) {
        System.out.println(
            "Get saveRoute request: authorUsername = " + request.getAuthorUsername() + "; route = "
                + request.getRoute());

        //TODO check existence
        RatingResponse response = ratingService.save(request);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/add-rate")
    public ResponseEntity<RatingResponse> addRate(@RequestBody RateRequest request) {
        System.out.println(
            "Get addRate request: id = " + request.getId() + "; rate = " + request.getRate());

        RatingResponse response = ratingService.addRate(request.getId(), request.getUsername(),
            request.getRate());
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-by-id")
    public ResponseEntity<RatingResponse> route(@RequestParam(name = "id") UUID routeId) {
        System.out.println("Get route request: " + routeId);
        RatingResponse response = ratingService.findById(routeId);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-by-author")
    public ResponseEntity<List<RatingResponse>> listRoutesByAuthorUsername(
        @RequestParam(name = "author") String authorUsername) {
        System.out.println("get route request: " + authorUsername);
        List<RatingResponse> response = ratingService.findAllByAuthorUsername(authorUsername);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-all")
    public ResponseEntity<List<RatingResponse>> listRoutes() {
        System.out.println("Get listRoutes request");
        return ResponseEntity.ok(ratingService.findAll());
    }
}