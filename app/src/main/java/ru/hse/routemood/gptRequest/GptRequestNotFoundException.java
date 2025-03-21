package ru.hse.routemood.gptRequest;

public class GptRequestNotFoundException extends RuntimeException {

    GptRequestNotFoundException(Long id) {
        super("Could not find massage " + id);
    }
}
