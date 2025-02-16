package ru.hse.routemood.gpt;


import com.google.gson.Gson;
import java.util.Map;

public class JsonWorker {

    public static TokenStore getToken(String response) throws IllegalArgumentException {
        var map = new Gson().fromJson(response, Map.class);
        if (map.containsKey("iamToken")) {
            return new TokenStore(map.get("iamToken").toString());
        }

        throw new IllegalArgumentException(map.get("message").toString());
    }
}
