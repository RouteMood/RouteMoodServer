package ru.hse.routemood.gpt;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GptHandler {

    public static TokenStore getIamToken(TokenStore oauthToken) {
        try (HttpClient client = HttpClient.newHttpClient();) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://iam.api.cloud.yandex.net/iam/v1/tokens"))
                .POST(HttpRequest.BodyPublishers.ofString(JsonBuilder.getTokenJson(oauthToken)))
                .build();

            HttpResponse<String> stringHttpResponse = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            return JsonWorker.getToken(stringHttpResponse.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String queryToGPT(TokenStore iamToken, TokenStore folderToken, String message) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://llm.api.cloud.yandex.net/foundationModels/v1/completion"))
                .POST(HttpRequest.BodyPublishers.ofString(
                    JsonBuilder.getQueryJson(folderToken, message)))
                .setHeader("Authorization", "Bearer " + iamToken.getToken())
                .setHeader("Content-type", "application/json")
                .build();

            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
