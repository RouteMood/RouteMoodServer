package ru.hse.routemood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
public class ServerApp {

    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }

    @GetMapping(path = "/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name,
        @RequestParam(value = "location", defaultValue = "Russia") String location) {
        return String.format("Hello, %s from %s!", name, location);
    }
}