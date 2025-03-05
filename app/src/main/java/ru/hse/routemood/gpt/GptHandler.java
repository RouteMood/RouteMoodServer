package ru.hse.routemood.gpt;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

import ru.hse.routemood.gpt.JsonWorker.RouteItem;

public class GptHandler {
    private static final TokenStore iamToken;
    private static final TokenStore folderToken;
    public static String tokenFileName;

    static {
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(tokenFileName)) {
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

        iamToken = new TokenStore(properties.getProperty("oauth-token"));
        folderToken = new TokenStore(properties.getProperty("folder-token"));
    }

    public static TokenStore getIamToken(TokenStore oauthToken) {
        try (HttpClient client = HttpClient.newHttpClient();) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://iam.api.cloud.yandex.net/iam/v1/tokens"))
                .POST(HttpRequest.BodyPublishers.ofString(JsonBuilder.getTokenJson(oauthToken)))
                .build();

            HttpResponse<String> stringHttpResponse = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (stringHttpResponse.statusCode() != 200) {
                // TODO: Write better error handling
                throw new RuntimeException("Oops, something went wrong");
            }

            return JsonWorker.getToken(stringHttpResponse.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<RouteItem> queryToGPT(String message) {
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

            if (response.statusCode() != 200) {
                // TODO: Write better error handling
                throw new RuntimeException("Oops, something went wrong");
            }
            return JsonWorker.getGptAnswer(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
