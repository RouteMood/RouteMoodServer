package ru.hse.routemood.gptRequest;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GptRequestModelAssembler implements RepresentationModelAssembler<GptRequest, EntityModel<GptRequest>> {

    @Override
    public EntityModel<GptRequest> toModel(GptRequest GptRequest) {

        return EntityModel.of(GptRequest, //
                linkTo(methodOn(GptRequestController.class).one(GptRequest.getId())).withSelfRel(),
                linkTo(methodOn(GptRequestController.class).all()).withRel("GptRequests"));
    }
}