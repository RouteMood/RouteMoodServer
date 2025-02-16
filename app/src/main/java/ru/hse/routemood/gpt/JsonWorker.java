package ru.hse.routemood.gpt;


import com.google.gson.Gson;
import java.util.List;


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

    public static String getGptAnswer(String response) {
        AnswerForQuery answerForQuery = new Gson().fromJson(response, AnswerForQuery.class);
        if (answerForQuery.result.alternatives.getFirst() == null) {
            throw new RuntimeException("Bad answer from gpt, try later");
        }

        return answerForQuery.result.alternatives.getFirst().message.text;
    }
}
