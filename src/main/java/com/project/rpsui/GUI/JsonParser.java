package com.project.rpsui.GUI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    public static List<ScoreEntry> parseJson(String json) {
        List<ScoreEntry> scoreEntries = new ArrayList<>();

        try {
            // Parse JSON string into JsonNode object
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            // Get the "highScores" array
            JsonNode highScoresNode = rootNode.get("highScores");

            // Iterate over the highScores array and extract username and score
            for (JsonNode entryNode : highScoresNode) {
                String username = entryNode.get("username").asText();
                int score = entryNode.get("score").asInt();
                scoreEntries.add(new ScoreEntry(username, score));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scoreEntries;
    }

    public static void main(String[] args) {
        String json = "{\"highestScore\":1,\"totalScores\":6,\"highScores\":[{\"score\":1,\"username\":\"sjndkndsgsdfdnsa1\"},{\"score\":0,\"username\":\"sjndkndsgsdfdnsa\"},{\"score\":0,\"username\":\"siyan\"},{\"score\":0,\"username\":\"abdul\"},{\"score\":0,\"username\":\"abdul1\"},{\"score\":0,\"username\":\"srinath\"}]}";

        List<ScoreEntry> scoreEntries = parseJson(json);
        for (ScoreEntry entry : scoreEntries) {
            System.out.println("Username: " + entry.getUsername() + ", Score: " + entry.getScore());
        }
    }
}

class ScoreEntry {
    private String username;
    private int score;

    public ScoreEntry(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
