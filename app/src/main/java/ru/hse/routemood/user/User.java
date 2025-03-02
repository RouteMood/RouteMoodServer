package ru.hse.routemood.user;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public String getLogin() {
        return this.login;
    }

    public Long getId() {
        return this.id;
    }

    public String getRole() {
        return this.role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setRole(String role) {
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