package ru.hse.routemood;

import java.util.ArrayList;
import java.util.List;
import ru.hse.routemood.gpt.GptHandler;
import ru.hse.routemood.gpt.JsonWorker;
import ru.hse.routemood.gpt.JsonWorker.RouteItem;
import ru.hse.routemood.gptMessage.GptRequest;

public class ServerApp {

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new RuntimeException("No config file was given");
        }

        GptHandler.tokenFileName = args[0];
        GptHandler.init();
        System.out.println(GptHandler.makeRequest(
            GptRequest.builder()
                .latitude(59.92951508111041)
                .longitude(30.41197525476372)
                .request("Хочу крутой маршрут")
                .build()));
    }
}
