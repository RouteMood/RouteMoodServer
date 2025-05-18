package ru.hse.routemood.rating;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.hse.routemood.rating.dto.RatingRequest;
import ru.hse.routemood.rating.dto.RatingResponse;
import ru.hse.routemood.rating.models.RatingItem;

@Service
@AllArgsConstructor
public class RatingService {

    private final RatingServiceRepository ratingServiceRepository;

    private static List<RatingResponse> toResponse(List<RatingItem> items) {
        List<RatingResponse> result = new ArrayList<>();
        for (RatingItem item : items) {
            result.add(new RatingResponse(item));
        }
        return result;
    }

    public RatingResponse save(@NonNull RatingRequest request) {
        return new RatingResponse(ratingServiceRepository.save(RatingItem.builder()
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
        if (item.getUsernameToRate().containsKey(username)) {
            item.setRatesSum(item.getRatesSum() - item.getUsernameToRate().get(username));
        }
        item.setRatesSum(item.getRatesSum() + rate);
        item.getUsernameToRate().put(username, rate);
        ratingServiceRepository.save(item);
        return new RatingResponse(item);
    }

    public Integer getUserRate(@NonNull UUID routeId, @NonNull String username) {
        RatingItem item = ratingServiceRepository.findById(routeId).orElse(null);
        return item != null && item.getUsernameToRate().containsKey(username)
            ? item.getUsernameToRate().get(username) : null;
    }

    public RatingResponse findById(@NonNull UUID id) {
        RatingItem item = ratingServiceRepository.findById(id).orElse(null);
        return item == null ? null : new RatingResponse(item);
    }

    public List<RatingResponse> findAllByAuthorUsername(@NonNull String authorUsername) {
        return toResponse(ratingServiceRepository.findAllByAuthorUsername(authorUsername));
    }

    public List<RatingResponse> findAll() {
        return toResponse(ratingServiceRepository.findAll());
    }
}
