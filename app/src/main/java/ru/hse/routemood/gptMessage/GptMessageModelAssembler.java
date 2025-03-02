package ru.hse.routemood.gptMessage;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class GptMessageModelAssembler implements RepresentationModelAssembler<GptMessage, EntityModel<GptMessage>> {

    @Override
    public EntityModel<GptMessage> toModel(GptMessage GptMessage) {

        return EntityModel.of(GptMessage, //
                linkTo(methodOn(GptMessageController.class).one(GptMessage.getId())).withSelfRel(),
                linkTo(methodOn(GptMessageController.class).all()).withRel("GptMessages"));
    }
}