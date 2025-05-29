package ru.hse.routemood.rating;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
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
import org.springframework.data.util.Pair;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.rating.dto.PageResponse;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;
import ru.hse.routemood.rating.models.RatingItem;
import ru.hse.routemood.rating.repository.RatingServiceRepository;
import ru.hse.routemood.rating.services.RatingService;
import ru.hse.routemood.user.domain.dto.UserResponse;
import ru.hse.routemood.user.services.UserService;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingServiceRepository repository;
    @Mock
    private UserService userService;
    @InjectMocks
    private RatingService ratingService;

    private Object invokePrivateMethod(String methodName, Object... params)
        throws InvocationTargetException, IllegalAccessException {
        for (Method method : ratingService.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)
                && method.getParameterCount() == params.length) {
                method.setAccessible(true);
                return method.invoke(ratingService, params);
            }
        }
        return null;
    }

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
        UserResponse author = UserResponse.builder().username(request.getAuthorUsername()).build();

        when(repository.save(any(RatingItem.class))).thenReturn(savedItem);
        when(userService.getUserInfo(any(String.class))).thenReturn(author);

        RatingResponse response = ratingService.save(request);

        System.out.println(response);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getAuthorUsername(), response.getAuthor().getUsername());
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

    @Test
    void getFirstPage_shouldReturnPageResponse() {
        List<RatingItem> items = List.of(
            mock(RatingItem.class),
            mock(RatingItem.class)
        );
        when(repository.getFirstPage(anyInt())).thenReturn(items);
        when(items.get(1).getRating()).thenReturn(3.5);
        when(items.get(1).getId()).thenReturn(UUID.randomUUID());

        PageResponse response = ratingService.getFirstPage("user");

        assertEquals(2, response.getItems().size());
        assertNotNull(response.getNextPageToken());
    }

    @Test
    void getNextPage_shouldHandleValidToken()
        throws InvocationTargetException, IllegalAccessException {
        String validToken = (String) invokePrivateMethod("toPageToken", 4.5, UUID.randomUUID());
        List<RatingItem> items = List.of(mock(RatingItem.class));
        when(repository.getNextPage(anyDouble(), any(), anyInt())).thenReturn(items);

        PageResponse response = ratingService.getNextPage(validToken, "user");

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
    }

    @Test
    void getNextPage_shouldHandleInvalidToken() {
        PageResponse response = ratingService.getNextPage("invalid_token", "user");

        assertNull(response);
    }

    @Test
    void parsePageToken_shouldHandleValidToken()
        throws InvocationTargetException, IllegalAccessException {
        UUID expectedId = UUID.randomUUID();

        String token = (String) invokePrivateMethod("toPageToken", 4.5, expectedId);

        Pair<Double, UUID> result = (Pair<Double, UUID>) invokePrivateMethod("parsePageToken",
            token);

        assertNotNull(result);
        assertEquals(4.5, result.getFirst());
        assertEquals(expectedId, result.getSecond());
    }

    @Test
    void parsePageToken_shouldHandleInvalidFormats() {
        assertAll(
            () -> assertNull(invokePrivateMethod("parsePageToken", "invalid_base64")),
            () -> assertNull(invokePrivateMethod("parsePageToken",
                Base64.getUrlEncoder().encodeToString("invalid_format".getBytes()))),
            () -> assertNull(invokePrivateMethod("parsePageToken",
                Base64.getUrlEncoder().encodeToString("not_a_number|uuid".getBytes())))
        );
    }

    @Test
    void toPageToken_shouldGenerateValidToken()
        throws InvocationTargetException, IllegalAccessException {
        UUID id = UUID.randomUUID();
        double rating = 4.5;

        String token = (String) invokePrivateMethod("toPageToken", rating, id);

        Pair<Double, UUID> result = (Pair<Double, UUID>) invokePrivateMethod("parsePageToken",
            token);

        assertNotNull(result);
        assertEquals(rating, result.getFirst());
        assertEquals(id, result.getSecond());
    }

    @Test
    void toPageToken_shouldReturnNullForEmptyList()
        throws InvocationTargetException, IllegalAccessException {
        assertNull(invokePrivateMethod("toPageToken", List.of()));
    }
}
