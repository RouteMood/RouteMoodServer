package ru.hse.routemood.gpt;


import com.google.gson.Gson;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
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

    @AllArgsConstructor
    @Getter
    public static class RouteItem {

        private double latitude;
        private double longitude;

        @Override
        public String toString() {
            return "[latitude = " + latitude + ", longitude = " + longitude + "]";
        }
    }

    @Getter
    public static class RouteAnswer {

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

        RouteAnswer answer = new Gson().fromJson(result, RouteAnswer.class);

        return answer.getRoute();
    }
}
