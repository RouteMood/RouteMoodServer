package ru.hse.routemood.gpt;

public class GptMessage {

    public final String text;
    public final String role;

    public GptMessage(String message) {
        text = message;
        role = "user";
    }

    @Override
    public String toString() {
        return text;
    }
}
