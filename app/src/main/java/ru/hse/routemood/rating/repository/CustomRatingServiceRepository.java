package ru.hse.routemood.rating.repository;

import java.util.List;
import java.util.UUID;
import ru.hse.routemood.rating.models.RatingItem;

public interface CustomRatingServiceRepository {
    List<RatingItem> getFirstPage(int pageSize);
//    List<RatingItem> getNextPage(double lastRating, UUID lastId, int pageSize);
}