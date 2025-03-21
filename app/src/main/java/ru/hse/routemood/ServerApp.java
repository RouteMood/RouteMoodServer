package ru.hse.routemood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
