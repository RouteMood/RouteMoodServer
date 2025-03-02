package ru.hse.routemood.gptMessage;

class GptMessageNotFoundException extends RuntimeException {

    GptMessageNotFoundException(Long id) {
        super("Could not find User " + id);
    }
}
