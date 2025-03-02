package ru.hse.routemood.user;

class UserNotFoundException extends RuntimeException {

    UserNotFoundException(Long id) {
        super("Could not find User " + id);
    }
}
