package ru.hse.routemood.user;

public class UserNotFoundException extends RuntimeException {

    UserNotFoundException(Long id) {
        super("Could not find User " + id);
    }
}
