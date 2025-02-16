package ru.hse.routemood;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import ru.hse.routemood.gpt.GptHandler;
import ru.hse.routemood.gpt.TokenStore;

public class ServerApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("No config file was given");
        }

        Properties properties = new Properties();
        String fileName = args[0];

        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (properties.getProperty("oauth-token") == null) {
            throw new RuntimeException("No oauth-token");
        }

        if (properties.getProperty("folder-token") == null) {
            throw new RuntimeException("No folder-token");
        }

        TokenStore oauth = new TokenStore(properties.getProperty("oauth-token"));
        TokenStore folder = new TokenStore(properties.getProperty("folder-token"));

        System.out.println(GptHandler.queryToGPT(GptHandler.getIamToken((oauth)), folder,
            "напиши в виде json без описания маршрут с указанием координат каждого пункта, протяжённостью 2 км, если у меня хорошее настроение и я хочу избегать шумных мест, я нахожусь на координатах 59.92951508111041, 30.41197525476372"));
    }
}