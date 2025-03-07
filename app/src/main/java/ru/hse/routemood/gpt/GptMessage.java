package ru.hse.routemood.gpt;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import ru.hse.routemood.gpt.Route;

@Getter
@Setter
@Entity
public class GptMessage {
    public static final String DEFAULT_ROLE = "user";

    private @Id @GeneratedValue Long id;
    public String text;
    public String role = DEFAULT_ROLE;
//    @ElementCollection
//    public Route route;

    public GptMessage(String message) {
        text = message;
    }

    public GptMessage() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GptMessage that = (GptMessage) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, role);
    }

    @Override
    public String toString() {
        return text;
    }
}