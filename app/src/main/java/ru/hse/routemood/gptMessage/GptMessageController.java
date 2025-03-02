package ru.hse.routemood.gptMessage;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// tag::constructor[]
@RestController
class GptMessageController {

    private final GptMessageRepository repository;

    private final GptMessageModelAssembler assembler;

    GptMessageController(GptMessageRepository repository, GptMessageModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
    }
    // end::constructor[]

    // Aggregate root

    // tag::get-aggregate-root[]
    @GetMapping("/GptMessages")
    CollectionModel<EntityModel<GptMessage>> all() {

        List<EntityModel<GptMessage>> GptMessages = repository.findAll().stream() //
                .map(assembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(GptMessages, linkTo(methodOn(GptMessageController.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]

    // tag::post[]
    @PostMapping("/GptMessages")
    ResponseEntity<?> newGptMessage(@RequestBody GptMessage newGptMessage) {

        EntityModel<GptMessage> entityModel = assembler.toModel(repository.save(newGptMessage));

        // TODO: query to GPT

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
    // end::post[]

    // tag::post[]
    @PostMapping("/GptMessages")
    ResponseEntity<?> newGptMessage(@RequestBody String prompt) {

        EntityModel<GptMessage> entityModel = assembler.toModel(repository.save(new GptMessage(prompt)));

        // TODO: query to GPT

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
    // end::post[]

    // Single item

    // tag::get-single-item[]
    @GetMapping("/GptMessages/{id}")
    EntityModel<GptMessage> one(@PathVariable Long id) {

        GptMessage GptMessage = repository.findById(id) //
                .orElseThrow(() -> new GptMessageNotFoundException(id));

        return assembler.toModel(GptMessage);
    }
    // end::get-single-item[]
}