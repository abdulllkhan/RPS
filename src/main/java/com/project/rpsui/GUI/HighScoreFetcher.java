package com.project.rpsui.GUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HighScoreFetcher {

    public static List<String> fetchHighScores() {
        List<String> highScores = new ArrayList<>();

        // Define the URL of the API endpoint
        String apiUrl = "http://localhost:8080/api/user/highscore";

        try {
            // Create a URL object
            URL url = new URL(apiUrl);

            // Open an HttpURLConnection to the API
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            conn.setRequestMethod("GET");

            // Get the response code from the server
            int responseCode = conn.getResponseCode();

            // Check if the response code is 200 (OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response body into a string
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parse the JSON response into a list of high scores
                // Here you need to parse the JSON response and extract high scores
                // For simplicity, I'm just returning the response as a single element list
                highScores.add(response.toString());
            } else {
                // Handle the error response
                System.out.println("Error: HTTP " + responseCode);
            }
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }

        return highScores;
    }
}
