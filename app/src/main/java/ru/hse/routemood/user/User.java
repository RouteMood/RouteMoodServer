package ru.hse.routemood.user;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "USER_") // "User" is a reserved keyword in H2 DB
class User {

    private @Id @GeneratedValue Long id;
    private String login;
    private String role;

    public User() {}

    public User(String login, String role) {
        this.login = login;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id) && Objects.equals(this.login, user.login)
                && Objects.equals(this.role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.login, this.role);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + this.id + ", firstName='" + this.login + '\'' + ", role='" + this.role + '\'' + '}';
    }
}