package ru.hse.routemood.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// tag::constructor[]
@RestController
public class UserController {

    private final UserRepository repository;

    private final UserModelAssembler assembler;

    UserController(UserRepository repository, UserModelAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
    }
    // end::constructor[]

    // Aggregate root

    // tag::get-aggregate-root[]
    @GetMapping("/Users")
    CollectionModel<EntityModel<User>> all() {

        List<EntityModel<User>> Users = repository.findAll().stream() //
                .map(assembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(Users, linkTo(methodOn(UserController.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]

    // tag::post[]
    @PostMapping("/Users")
    ResponseEntity<?> newUser(@RequestBody User newUser) {

        EntityModel<User> entityModel = assembler.toModel(repository.save(newUser));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
    // end::post[]

    // Single item

    // tag::get-single-item[]
    @GetMapping("/Users/{id}")
    EntityModel<User> one(@PathVariable Long id) {

        User User = repository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));

        return assembler.toModel(User);
    }
    // end::get-single-item[]

    // tag::put[]
    @PutMapping("/Users/{id}")
    ResponseEntity<?> replaceUser(@RequestBody User newUser, @PathVariable Long id) {

        User updatedUser = repository.findById(id) //
                .map(User -> {
                    User.setLogin(newUser.getLogin());
                    User.setRole(newUser.getRole());
                    return repository.save(User);
                }) //
                .orElseGet(() -> {
                    return repository.save(newUser);
                });

        EntityModel<User> entityModel = assembler.toModel(updatedUser);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
    // end::put[]

    // tag::delete[]
    @DeleteMapping("/Users/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    // end::delete[]
}