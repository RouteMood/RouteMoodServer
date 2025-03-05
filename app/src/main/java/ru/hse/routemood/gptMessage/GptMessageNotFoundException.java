package ru.hse.routemood.gptMessage;

public class GptMessageNotFoundException extends RuntimeException {

    GptMessageNotFoundException(Long id) {
        super("Could not find User " + id);
    }
}
