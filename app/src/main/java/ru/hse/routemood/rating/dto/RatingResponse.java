package ru.hse.routemood.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.rating.models.RatingItem;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {

    public double rating;
    public String authorUsername;
    public Route route;

    public RatingResponse(RatingItem ratingItem) {
        this.rating = ratingItem.getRating();
        this.authorUsername = ratingItem.getAuthorUsername();
        this.route = RatingItem.toRoute(ratingItem.getRoute());
    }
}