package ru.hse.routemood.gpt;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.hse.routemood.gpt.JsonWorker.RouteItem;
import ru.hse.routemood.gptMessage.GptRequest;

public class GptHandler {

    private static TokenStore oauthToken;
    private static TokenStore folderToken;
    private static TokenStore apiToken;
    public static String tokenFileName;
    private static final String requestTemplate = "Создай пешеходный маршрут длиной примерно 5 км и выведи его в формате json без фразы ```json, где будет поле \"route\", в котором будет массив из координат маршрута, начинающийся в координатах %s, %s. Учти, что %s";
    private static final String routeRequestTemplate = "https://api.geoapify.com/v1/routing?waypoints=%s&mode=walk&apiKey=%s";
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();


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

        if (properties.getProperty("route-token") == null) {
            throw new RuntimeException("No route-token");
        }

        oauthToken = new TokenStore(properties.getProperty("oauth-token"));
        folderToken = new TokenStore(properties.getProperty("folder-token"));
        apiToken = new TokenStore(properties.getProperty("route-token"));
    }

    private static String formatRoute(List<RouteItem> routeItems) {
        return routeItems.stream()
            .map(item -> item.getLatitude() + "%2C" + item.getLongitude())
            .collect(Collectors.joining("%7C"));
    }

    private static String makeCorrectUrl(List<RouteItem> routeItems) {
        return String.format(routeRequestTemplate, formatRoute(routeItems), apiToken.getToken());
    }

    private static String makeRouteRequest(List<RouteItem> routeItems) {
        Request request = new Request.Builder()
            .url(makeCorrectUrl(routeItems))
            .method("GET", null)
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<RouteItem> makeRequest(GptRequest request) {
        TokenStore iamToken = getIamToken(oauthToken);
        if (iamToken == null) {
            return null;
        }

        String message = String.format(requestTemplate, request.getLatitude(),
            request.getLongitude(), request.getRequest());

        List<RouteItem> items = queryToGPT(iamToken, message);

        if (items == null) {
            return null;
        }

        String json = makeRouteRequest(items);

        if (json == null) {
            return null;
        }

        return JsonWorker.applyRoute(json);
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
                return null;
            }

            return JsonWorker.getToken(stringHttpResponse.body());
        } catch (IOException | InterruptedException e) {
            return null;
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
                return null;
            }
            return JsonWorker.getGptAnswer(response.body());
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}
