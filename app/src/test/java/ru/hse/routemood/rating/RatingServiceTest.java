package ru.hse.routemood.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;
import ru.hse.routemood.rating.models.RatingItem;
import ru.hse.routemood.rating.repository.RatingServiceRepository;
import ru.hse.routemood.rating.services.RatingService;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingServiceRepository repository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void save_ValidRequest_ReturnsResponse() {
        RatingRequest request = new RatingRequest(
            "Test Route", "Description", "user", Route.builder().build()
        );
        RatingItem savedItem = RatingItem.builder()
            .name(request.getName())
            .description(request.getDescription())
            .authorUsername(request.getAuthorUsername())
            .build();

        when(repository.save(any(RatingItem.class))).thenReturn(savedItem);

        RatingResponse response = ratingService.save(request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getAuthorUsername(), response.getAuthorUsername());
        verify(repository).save(any(RatingItem.class));
    }

    @Test
    void delete_ExistingId_ReturnsTrue() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        boolean result = ratingService.delete(id);

        assertTrue(result);
        verify(repository).deleteById(id);
    }

    @Test
    void delete_NonExistingId_ReturnsFalse() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        boolean result = ratingService.delete(id);

        assertFalse(result);
        verify(repository, never()).deleteById(id);
    }

    @Test
    void addRate_NewUser_UpdatesRating() {
        UUID routeId = UUID.randomUUID();
        RatingItem item = RatingItem.builder()
            .usernameToRate(new HashMap<>())
            .ratesSum(0)
            .build();

        when(repository.findById(routeId)).thenReturn(Optional.of(item));

        ratingService.addRate(routeId, "newUser", 5);

        assertEquals(5, item.getRatesSum());
        assertEquals(5, item.getUsernameToRate().get("newUser"));
        verify(repository).save(item);
    }

    @Test
    void addRate_ExistingUser_UpdatesRating() {
        UUID routeId = UUID.randomUUID();
        Map<String, Integer> rates = new HashMap<>();
        rates.put("user", 3);
        RatingItem item = RatingItem.builder()
            .usernameToRate(rates)
            .ratesSum(3)
            .build();

        when(repository.findById(routeId)).thenReturn(Optional.of(item));

        ratingService.addRate(routeId, "user", 5);

        assertEquals(5, item.getRatesSum());
        assertEquals(5, item.getUsernameToRate().get("user"));
        verify(repository).save(item);
    }

    @Test
    void addRate_NonExistingRoute_ReturnsNull() {
        UUID routeId = UUID.randomUUID();
        when(repository.findById(routeId)).thenReturn(Optional.empty());

        RatingResponse response = ratingService.addRate(routeId, "user", 5);

        assertNull(response);
        verify(repository, never()).save(any());
    }

    @Test
    void getUserRate_ExistingUser_ReturnsRate() {
        UUID routeId = UUID.randomUUID();
        RatingItem item = RatingItem.builder()
            .usernameToRate(Map.of("user", 4))
            .build();
        when(repository.findById(routeId)).thenReturn(Optional.of(item));

        Integer rate = ratingService.getUserRate(routeId, "user");

        assertEquals(4, rate);
    }

    @Test
    void getUserRate_NonExistingUser_ReturnsNull() {
        UUID routeId = UUID.randomUUID();
        RatingItem item = RatingItem.builder()
            .usernameToRate(new HashMap<>())
            .build();
        when(repository.findById(routeId)).thenReturn(Optional.of(item));

        Integer rate = ratingService.getUserRate(routeId, "user");

        assertNull(rate);
    }

    @Test
    void getUserRate_NonExistingRoute_ReturnsNull() {
        UUID routeId = UUID.randomUUID();
        when(repository.findById(routeId)).thenReturn(Optional.empty());

        Integer rate = ratingService.getUserRate(routeId, "user");

        assertNull(rate);
    }

    @Test
    void findById_ExistingId_ReturnsResponse() {
        UUID id = UUID.randomUUID();
        RatingItem item = new RatingItem();
        when(repository.findById(id)).thenReturn(Optional.of(item));

        RatingResponse response = ratingService.findById(id);

        assertNotNull(response);
    }

    @Test
    void findById_NonExistingId_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        RatingResponse response = ratingService.findById(id);

        assertNull(response);
    }

    @Test
    void findAllByAuthorUsername_ValidUser_ReturnsResponses() {
        String author = "user";
        List<RatingItem> items = List.of(new RatingItem(), new RatingItem());
        when(repository.findAllByAuthorUsername(author)).thenReturn(items);

        List<RatingResponse> responses = ratingService.findAllByAuthorUsername(author);

        assertEquals(items.size(), responses.size());
    }

    @Test
    void findAll_ReturnsAllResponses() {
        List<RatingItem> items = List.of(new RatingItem(), new RatingItem());
        when(repository.findAll()).thenReturn(items);

        List<RatingResponse> responses = ratingService.findAll();

        assertEquals(items.size(), responses.size());
    }
}
