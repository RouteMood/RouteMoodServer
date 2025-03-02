package ru.hse.routemood.user;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User User) {

        return EntityModel.of(User, //
                linkTo(methodOn(UserController.class).one(User.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).all()).withRel("Users"));
    }
}