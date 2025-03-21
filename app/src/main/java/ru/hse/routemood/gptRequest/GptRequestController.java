package ru.hse.routemood.gptRequest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.routemood.gpt.GptHandler;
import ru.hse.routemood.gpt.JsonWorker.Route;
import ru.hse.routemood.gpt.JsonWorker.RouteItem;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class GptRequestController {
    @PostMapping("/gpt-request")
    ResponseEntity<Route> newGptRequest(@RequestBody GptRequest request) {
        return ResponseEntity.ok(GptHandler.makeRequest(
                GptRequest.builder().latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .request(request.getRequest()).
                        build()));
    }

    @GetMapping("/gpt-fictive-message")
    ResponseEntity<Route> fictiveMessage(
            @RequestParam(name = "longitude") Double longitude,
            @RequestParam(name = "latitude") Double latitude,
            @RequestParam(name = "request") String request) {

        Double[][] array = {{59.92951508111041, 30.41197525476372},
                {59.930146, 30.409942},
                {59.931028, 30.408584},
                {59.932061, 30.407955},
                {59.933068, 30.408038},
                {59.93405, 30.408656},
                {59.935008, 30.409826},
                {59.93603, 30.412245}};

        List<RouteItem> result = new ArrayList<>();
        for (Double[] doubles : array) {
            result.add(new RouteItem(doubles[0], doubles[1]));
        }

        return ResponseEntity.ok(Route.builder().route(result).build());
    }
}