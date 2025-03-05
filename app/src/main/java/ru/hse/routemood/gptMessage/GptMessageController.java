package ru.hse.routemood.gptMessage;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.routemood.gpt.GptHandler;
import ru.hse.routemood.gpt.GptMessage;
import ru.hse.routemood.gpt.JsonWorker.RouteItem;

@RestController
@AllArgsConstructor
public class GptMessageController {

    private final GptMessageRepository repository;
    private final GptMessageModelAssembler assembler;

    @GetMapping("/GptMessage")
    ResponseEntity<List<RouteItem>> newGptMessage(
        @RequestParam(name = "longitude") Double longitude,
        @RequestParam(name = "latitude") Double latitude,
        @RequestParam(name = "request") String request) {
        return ResponseEntity.ok(GptHandler.makeRequest(
            GptRequest.builder().latitude(latitude).longitude(longitude).request(request).build()));
    }

    @GetMapping("/GptMessages")
    CollectionModel<EntityModel<GptMessage>> all() {

        List<EntityModel<GptMessage>> GptMessages = repository.findAll().stream() //
            .map(assembler::toModel) //
            .collect(Collectors.toList());

        return CollectionModel.of(GptMessages,
            linkTo(methodOn(GptMessageController.class).all()).withSelfRel());
    }

    @GetMapping("/GptMessages/{id}")
    EntityModel<GptMessage> one(@PathVariable Long id) {

        GptMessage GptMessage = repository.findById(id) //
            .orElseThrow(() -> new GptMessageNotFoundException(id));

        return assembler.toModel(GptMessage);
    }
}