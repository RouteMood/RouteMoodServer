package ru.hse.routemood.rating.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.gpt.JsonWorker.RouteItem;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RatingItem {

    public String name;
    public String description;
    public String authorUsername;
    @ElementCollection
    public List<Double> route; // [latitude_0, longitude_0, latitude_1, longitude_1, ...]
    @Builder.Default
    private int ratesSum = 0;
    @Builder.Default
    @ElementCollection
    private Map<String, Integer> usernameToRate = new HashMap<>();
    @Builder.Default
    private double rating = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    public static List<Double> fromRoute(Route route) {
        if (route.getRoute() == null) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (RouteItem item : route.getRoute()) {
            result.add(item.getLatitude());
            result.add(item.getLongitude());
        }
        return result;
    }

    public static Route toRoute(List<Double> route) {
        List<RouteItem> result = new ArrayList<>();
        if (route != null) {
            for (int i = 0; i < route.size(); i += 2) {
                result.add(new RouteItem(route.get(i), route.get(i + 1)));
            }
        }
        return Route.builder().route(result).build();
    }

    public void updateRating(@NonNull String username, int rate) {
        if (usernameToRate.containsKey(username)) {
            ratesSum -= usernameToRate.get(username);
        }
        ratesSum += rate;
        usernameToRate.put(username, rate);
        rating = (double) ratesSum / usernameToRate.size();
    }
}
