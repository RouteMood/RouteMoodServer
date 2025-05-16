package ru.hse.routemood.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hse.routemood.gpt.JsonWorker.Route;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {

    public String name;
    public String description;
    public String authorUsername;
    public Route route;
}
