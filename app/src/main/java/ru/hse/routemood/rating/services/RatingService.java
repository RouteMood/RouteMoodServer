package ru.hse.routemood.rating.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.hse.routemood.rating.dto.PageResponse;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;
import ru.hse.routemood.rating.models.RatingItem;
import ru.hse.routemood.rating.repository.RatingServiceRepository;
import ru.hse.routemood.user.services.UserService;

@Service
@AllArgsConstructor
public class RatingService {

    private final RatingServiceRepository ratingServiceRepository;
    private final UserService userService;
    private final int PAGE_SIZE = 10;

    private RatingResponse toResponse(RatingItem item) {
        RatingResponse response = new RatingResponse(item);
        response.setAuthor(userService.getUserInfo(item.getAuthorUsername()));
        return response;
    }

    private RatingResponse toResponse(RatingItem item, String clientUsername) {
        RatingResponse response = new RatingResponse(item, clientUsername);
        response.setAuthor(userService.getUserInfo(item.getAuthorUsername()));
        return response;
    }

    private List<RatingResponse> toResponse(List<RatingItem> items) {
        List<RatingResponse> result = new ArrayList<>();
        for (RatingItem item : items) {
            result.add(toResponse(item));
        }
        return result;
    }

    private List<RatingResponse> toResponse(List<RatingItem> items, String clientUsername) {
        List<RatingResponse> result = new ArrayList<>();
        for (RatingItem item : items) {
            result.add(toResponse(item, clientUsername));
        }
        return result;
    }

    public RatingResponse save(@NonNull RatingRequest request) {
        return toResponse(ratingServiceRepository.save(RatingItem.builder()
            .name(request.getName())
            .description(request.getDescription())
            .authorUsername(request.getAuthorUsername())
            .route(RatingItem.fromRoute(request.getRoute()))
            .build()));
    }

    public boolean delete(@NonNull UUID id) {
        if (ratingServiceRepository.existsById(id)) {
            ratingServiceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public RatingResponse addRate(@NonNull UUID routeId, @NonNull String username,
        @NonNull int rate) {
        RatingItem item = ratingServiceRepository.findById(routeId).orElse(null);

        if (item == null) {
            return null;
        }
        item.updateRating(username, rate);

        ratingServiceRepository.save(item);

        return toResponse(item);
    }

    public Integer getUserRate(@NonNull UUID routeId, @NonNull String username) {
        RatingItem item = ratingServiceRepository.findById(routeId).orElse(null);
        return item != null && item.getUsernameToRate().containsKey(username)
            ? item.getUsernameToRate().get(username) : null;
    }

    public RatingResponse findById(@NonNull UUID id) {
        RatingItem item = ratingServiceRepository.findById(id).orElse(null);
        return item == null ? null : toResponse(item);
    }

    public RatingResponse findById(@NonNull UUID id, @NonNull String clientUsername) {
        RatingItem item = ratingServiceRepository.findById(id).orElse(null);
        return item == null ? null : toResponse(item, clientUsername);
    }

    public List<RatingResponse> findAllByAuthorUsername(@NonNull String authorUsername) {
        return toResponse(ratingServiceRepository.findAllByAuthorUsername(authorUsername));
    }

    public List<RatingResponse> findAllByAuthorUsername(@NonNull String authorUsername,
        @NonNull String clientUsername) {
        List<RatingItem> items = ratingServiceRepository.findAllByAuthorUsername(authorUsername);
        return toResponse(items, clientUsername);
    }

    public List<RatingResponse> findAll() {
        return toResponse(ratingServiceRepository.findAll());
    }

    public List<RatingResponse> findAll(@NonNull String clientUsername) {
        List<RatingItem> items = ratingServiceRepository.findAll();
        return toResponse(items, clientUsername);
    }

    public PageResponse getFirstPage(String clientUsername) {
        List<RatingItem> items = ratingServiceRepository.getFirstPage(PAGE_SIZE);
        return PageResponse.builder()
            .items(toResponse(items, clientUsername))
            .nextPageToken(toPageToken(items))
            .build();
    }

    public PageResponse getNextPage(@NonNull String token, String clientUsername) {
        Pair<Double, UUID> pairFromToken = parsePageToken(token);
        if (pairFromToken == null) {
            return null;
        }
        List<RatingItem> items = ratingServiceRepository.getNextPage(pairFromToken.getFirst(),
            pairFromToken.getSecond(), PAGE_SIZE);
        return PageResponse.builder()
            .items(toResponse(items, clientUsername))
            .nextPageToken(toPageToken(items))
            .build();
    }

    private Pair<Double, UUID> parsePageToken(@NonNull String token) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            String decodedToken = new String(decodedBytes, StandardCharsets.UTF_8);

            int splitIndex = decodedToken.indexOf('|');
            if (splitIndex == -1) {
                return null;
            }

            double rating = Double.parseDouble(decodedToken.substring(0, splitIndex));
            UUID id = UUID.fromString(decodedToken.substring(splitIndex + 1));

            return Pair.of(rating, id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String toPageToken(List<RatingItem> items) {
        return items == null || items.isEmpty() ? null
            : toPageToken(items.getLast().getRating(), items.getLast().getId());
    }

    private String toPageToken(double lastRating, UUID lastId) {
        String rawToken = lastRating + "|" + lastId;

        byte[] bytes = rawToken.getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
