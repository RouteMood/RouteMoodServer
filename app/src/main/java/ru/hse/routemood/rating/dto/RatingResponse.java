package ru.hse.routemood.rating.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.rating.models.RatingItem;
import ru.hse.routemood.user.domain.dto.UserResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {

    public UUID id;
    public String name;
    public String description;
    public double rating;
    public UserResponse author;
    public Route route;
    public Integer rate;

    public RatingResponse(RatingItem ratingItem) {
        this.id = ratingItem.getId();
        this.name = ratingItem.getName();
        this.description = ratingItem.getDescription();
        this.rating = ratingItem.getRating();
        this.route = RatingItem.toRoute(ratingItem.getRoute());
    }

    public RatingResponse(RatingItem ratingItem, @NonNull String username) {
        this(ratingItem);
        this.rate = ratingItem.getUsernameToRate().get(username);
    }
}