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
import ru.hse.routemood.gptRequest.GptRequest;

public class GptHandler {
    private static TokenStore ouathToken;
    private static TokenStore folderToken;
    public static String tokenFileName;
    private static final String requestTemplate = "Создай пешеходный маршрут длиной примерно 5 км и выведи его в формате json без фразы ```json, где будет поле \"route\", в котором будет массив из координат маршрута, начинающийся в координатах %s, %s. Учти, что %s";

    public static void init() {
        Properties properties = new Properties();
        System.out.println(tokenFileName);

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

        ouathToken = new TokenStore(properties.getProperty("oauth-token"));
        folderToken = new TokenStore(properties.getProperty("folder-token"));
        System.out.println(ouathToken);
        System.out.println(folderToken);
    }

    public static List<RouteItem> makeRequest(GptRequest request) {
        TokenStore iamToken = getIamToken(ouathToken);
        String message = String.format(requestTemplate, request.getLatitude(), request.getLongitude(), request.getRequest());
        return queryToGPT(iamToken, message);
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

    public static List<RouteItem> queryToGPT(TokenStore iamToken, String message) {
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
