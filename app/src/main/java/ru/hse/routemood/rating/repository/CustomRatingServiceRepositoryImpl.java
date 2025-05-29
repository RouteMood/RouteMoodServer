package ru.hse.routemood.rating.repository;

import static ru.hse.routemood.jooq.tables.Ratingitem.RATINGITEM;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.hse.routemood.rating.models.RatingItem;

@Repository
@RequiredArgsConstructor
public class CustomRatingServiceRepositoryImpl implements CustomRatingServiceRepository {

    private final DSLContext dslContext;

    @Override
    public List<RatingItem> getFirstPage(int pageSize) {
        return dslContext.selectFrom(RATINGITEM)
            .orderBy(RATINGITEM.RATING.desc(), RATINGITEM.ID.desc())
            .limit(pageSize)
            .fetchInto(RatingItem.class);
    }

//    @Override
//    public List<RatingItem> getNextPage(double lastRating, UUID lastId, int pageSize) {
//        return dslContext.selectFrom(RATINGITEM)
//            .where(DSL.row(RATINGITEM.RATING, RATINGITEM.ID).lessThan(DSL.row(lastRating, lastId)))
//            .orderBy(RATINGITEM.RATING.desc(), RATINGITEM.ID.desc())
//            .limit(pageSize)
//            .fetchInto(RatingItem.class);
//    }
}