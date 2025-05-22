package ru.hse.routemood.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hse.routemood.auth.services.JwtService;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.gpt.JsonWorker.RouteItem;
import ru.hse.routemood.rating.dto.RateRequest;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    private final RatingResponse defaultTestRatingResponse = RatingResponse.builder()
        .id(UUID.randomUUID())
        .name("testName")
        .description("testDescription")
        .rating(5.0)
        .authorUsername("testUser")
        .route(Route.builder()
            .route(Collections.singletonList(new RouteItem(0.0, 0.0)))
            .build())
        .rate(5)
        .build();
    @Mock
    private RatingService ratingService;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private RatingController ratingController;

    @Test
    void saveRoute_Success() {
        when(ratingService.save(any())).thenReturn(defaultTestRatingResponse);

        ResponseEntity<RatingResponse> response = ratingController.saveRoute(new RatingRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(defaultTestRatingResponse, response.getBody());
    }

    @Test
    void saveRoute_BadRequest() {
        when(ratingService.save(any())).thenReturn(null);

        ResponseEntity<RatingResponse> response = ratingController.saveRoute(new RatingRequest());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteRating_Success() {
        UUID id = UUID.randomUUID();
        when(ratingService.delete(id)).thenReturn(true);

        ResponseEntity<Void> response = ratingController.deleteRating(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteRating_NotFound() {
        UUID id = UUID.randomUUID();
        when(ratingService.delete(id)).thenReturn(false);

        ResponseEntity<Void> response = ratingController.deleteRating(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addRate_Success() {
        RateRequest request = new RateRequest();
        request.setId(UUID.randomUUID());
        when(ratingService.addRate(any(), any(), anyInt())).thenReturn(defaultTestRatingResponse);

        ResponseEntity<RatingResponse> response = ratingController.addRate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(defaultTestRatingResponse, response.getBody());
    }

    @Test
    void addRate_NotFound() {
        RateRequest request = new RateRequest();
        when(ratingService.addRate(any(), any(), anyInt())).thenReturn(null);

        ResponseEntity<RatingResponse> response = ratingController.addRate(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getById_Success() {
        UUID routeId = UUID.randomUUID();
        String authHeader = "Bearer token";
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(ratingService.findById(routeId, "user")).thenReturn(defaultTestRatingResponse);

        ResponseEntity<RatingResponse> response = ratingController.route(routeId, authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(defaultTestRatingResponse, response.getBody());
    }

    @Test
    void getById_NotFound() {
        UUID routeId = UUID.randomUUID();
        String authHeader = "Bearer token";
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(ratingService.findById(routeId, "user")).thenReturn(null);

        ResponseEntity<RatingResponse> response = ratingController.route(routeId, authHeader);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getByAuthor_Success() {
        String author = "author";
        String authHeader = "Bearer token";
        List<RatingResponse> expected = Collections.singletonList(defaultTestRatingResponse);
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(ratingService.findAllByAuthorUsername(author, "user")).thenReturn(expected);

        ResponseEntity<List<RatingResponse>> response =
            ratingController.listRoutesByAuthorUsername(author, authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getByAuthor_NotFound() {
        String author = "author";
        String authHeader = "Bearer token";
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(ratingService.findAllByAuthorUsername(author, "user")).thenReturn(null);

        ResponseEntity<List<RatingResponse>> response =
            ratingController.listRoutesByAuthorUsername(author, authHeader);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAll_Success() {
        String authHeader = "Bearer token";
        List<RatingResponse> expected = Collections.singletonList(defaultTestRatingResponse);
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(ratingService.findAll("user")).thenReturn(expected);

        ResponseEntity<List<RatingResponse>> response =
            ratingController.listRoutes(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getAll_EmptyList() {
        String authHeader = "Bearer token";
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(ratingService.findAll("user")).thenReturn(Collections.emptyList());

        ResponseEntity<List<RatingResponse>> response =
            ratingController.listRoutes(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
