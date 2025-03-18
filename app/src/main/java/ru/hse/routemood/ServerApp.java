package ru.hse.routemood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Handler;

import ru.hse.routemood.gpt.GptHandler;
import ru.hse.routemood.gpt.TokenStore;

@SpringBootApplication
public class ServerApp {
    public static void main(String[] args) {
//        if (args.length < 1) {
//            throw new RuntimeException("No config file was given");
//        }
//        GptHandler.tokenFileName = args[0];
//        GptHandler.init();
        SpringApplication.run(ServerApp.class, args);
    }
}
