package ru.hse.routemood.gpt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonBuilder {

    private static class GetQueryStructure {

        private static class CompleteionOptions {

            private final boolean stream = false;
            private final Double temperature = 0.5;
            private final String maxTokens = "2";
            private final Map<String, String> reasoningOptions = Map.of("mode", "DISABLED");
        }

        private String modelUri;
        private CompleteionOptions completeionOptions;
        private List<GptMessage> messages;

        public GetQueryStructure(String uri, String message) {
            modelUri = uri;
            completeionOptions = new CompleteionOptions();
            messages = List.of(new GptMessage(message));
        }
    }

    public static String getTokenJson(TokenStore token) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("yandexPassportOauthToken", token.getToken());
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static String getQueryJson(TokenStore token, String query) {
        return new GsonBuilder().setPrettyPrinting().create()
            .toJson(new GetQueryStructure("gpt://" + token.getToken() + "/yandexgpt/rc", query));
    }
}
