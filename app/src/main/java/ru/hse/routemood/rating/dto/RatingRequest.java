package ru.hse.routemood.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hse.routemood.gpt.JsonWorker.Route;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {

    public String name;
    public String description;
    public String authorUsername;
    public Route route;
}
