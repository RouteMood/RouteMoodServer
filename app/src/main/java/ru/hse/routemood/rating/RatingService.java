package ru.hse.routemood.rating;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
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

    public RatingItem save(RatingRequest request) {
        return ratingServiceRepository.save(RatingItem.builder()
            .authorUsername(request.getAuthorUsername())
            .route(RatingItem.fromRoute(request.getRoute()))
            .build());
    }

    public RatingResponse addRate(UUID id, int rate) {
        RatingItem item = ratingServiceRepository.findById(id).orElse(null);
        if (item == null) {
            return null;
        }
        item.setRatesCount(item.getRatesCount() + 1);
        item.setRatesSum(item.getRatesSum() + rate);
        ratingServiceRepository.save(item);
        return new RatingResponse(item);
    }

    public RatingResponse findById(UUID id) {
        RatingItem item = ratingServiceRepository.findById(id).orElse(null);
        return item == null ? null : new RatingResponse(item);
    }

    public List<RatingResponse> findAllByAuthorUsername(String authorUsername) {
        return toResponse(ratingServiceRepository.findAllByAuthorUsername(authorUsername));
    }

    public List<RatingResponse> findAll() {
        return toResponse(ratingServiceRepository.findAll());
    }
}
