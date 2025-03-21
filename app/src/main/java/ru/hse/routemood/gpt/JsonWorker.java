package ru.hse.routemood.gpt;


import com.google.gson.Gson;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class JsonWorker {

    private static class AnswerForToken {

        public String iamToken;
    }

    private static class AnswerForQuery {

        public static class Alternatives {

            public static class InMessage {

                public GptMessage message;
            }

            public List<InMessage> alternatives;

            @Override
            public String toString() {
                return alternatives.toString();
            }
        }

        public Alternatives result;

        @Override
        public String toString() {
            return result.alternatives.toString();
        }
    }

    public static TokenStore getToken(String response) {
        AnswerForToken map = new Gson().fromJson(response, AnswerForToken.class);
        return new TokenStore(map.iamToken);
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class RouteItem {

        private double latitude;
        private double longitude;


        @Override
        public String toString() {
            return "[latitude = " + latitude + ", longitude = " + longitude + "]";
        }
    }

    @Getter
    public static class Route {

        private List<RouteItem> route;

        @Override
        public String toString() {
            StringJoiner result = new StringJoiner("\n");
            for (RouteItem it : route) {
                result.add(it.toString());
            }
            return result.toString();
        }
    }

    public static List<RouteItem> getGptAnswer(String response) {
        AnswerForQuery answerForQuery = new Gson().fromJson(response, AnswerForQuery.class);
        if (answerForQuery.result.alternatives.getFirst() == null) {
            throw new RuntimeException("Bad answer from gpt, try later");
        }

        String result = answerForQuery.result.alternatives.getFirst().message.text.chars()
            .mapToObj(x -> String.valueOf((char) x)).filter(x -> !x.equals("`"))
            .collect(Collectors.joining());

        Route answer = new Gson().fromJson(result, Route.class);

        return answer.getRoute();
    }


    private static class RouteFeature {

        public List<FeatureElement> features;
    }

    private static class FeatureElement {

        public Geometry geometry;
    }

    private static class Geometry {

        public List<List<List<Double>>> coordinates;
    }

    public static List<RouteItem> applyRoute(String json) {
        RouteFeature items = new Gson().fromJson(json, RouteFeature.class);
        if (items.features == null || items.features.getFirst() == null) {
            return null;
        }

        return items.features.getFirst().geometry.coordinates.stream()
            .flatMap(
                legs ->
                    legs.stream().map(item -> RouteItem.builder()
                        .latitude(item.getLast())
                        .longitude(item.getFirst())
                        .build())
            )
            .toList();
    }
}
