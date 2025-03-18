package ru.hse.routemood.gptRequest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.routemood.gpt.GptHandler;
import ru.hse.routemood.gpt.Route;

@RestController
@AllArgsConstructor
public class GptRequestController {

    private final GptRequestRepository repository;
    private final GptRequestModelAssembler assembler;

    @GetMapping("/gpt-request")
    ResponseEntity<Route> newGptRequest(
        @RequestParam(name = "longitude") Double longitude,
        @RequestParam(name = "latitude") Double latitude,
        @RequestParam(name = "request") String request) {
        return ResponseEntity.ok(GptHandler.makeRequest(
            GptRequest.builder().latitude(latitude).longitude(longitude).request(request).build()));
    }

    @GetMapping("/gpt-requests")
    CollectionModel<EntityModel<GptRequest>> all() {

        List<EntityModel<GptRequest>> GptRequests = repository.findAll().stream() //
            .map(assembler::toModel) //
            .collect(Collectors.toList());

        return CollectionModel.of(GptRequests,
                linkTo(methodOn(GptRequestController.class).all()).withSelfRel());
    }

    // tag::post[]
    @PostMapping("/gpt-request")
    ResponseEntity<Route> newGptRequest(@RequestBody GptRequest request) {
        return ResponseEntity.ok(GptHandler.makeRequest(
                GptRequest.builder().latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .request(request.getRequest()).
                        build()));
    }
    // end::post[]

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

        List<Route.RouteItem> result = new ArrayList<>();
        for (Double[] doubles : array) {
            result.add(new Route.RouteItem(doubles[0], doubles[1]));
        }

        return ResponseEntity.ok(new Route(result));
    }

    @GetMapping("/gpt-requests/{id}")
    EntityModel<GptRequest> one(@PathVariable Long id) {

        GptRequest GptRequest = repository.findById(id) //
                .orElseThrow(() -> new GptRequestNotFoundException(id));

        return assembler.toModel(GptRequest);
    }
}