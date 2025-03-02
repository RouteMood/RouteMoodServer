package ru.hse.routemood.gptMessage;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

import ru.hse.routemood.gpt.RouteItem;

@Entity
public class GptMessage {
    public static final String DEFAULT_ROLE = "user";

    private @Id @GeneratedValue Long id;
    public String text;
    public String role = DEFAULT_ROLE;
    @OneToMany
    public List<RouteItem> route;

    public GptMessage(String message) {
        text = message;
    }

    public GptMessage() {}

    public String getRole() {
        return role;
    }

    public String getText() {
        return text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRole(String role) {
        this.role = role;
    }

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