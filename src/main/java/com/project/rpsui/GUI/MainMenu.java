package com.project.rpsui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainMenu extends JFrame {
    private JLabel highScoresLabel;

    public MainMenu() {
        setTitle("Game Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create buttons
        JButton startGameButton = new JButton("Start Game");
        JButton enterGameButton = new JButton("Enter Game");
        JButton viewHighScoresButton = new JButton("View High Scores");

        // Set size of the window
        setSize(500, 400);

        // Add action listeners to buttons
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add code to handle "Start Game" button click
                JOptionPane.showMessageDialog(MainMenu.this, "Start Game button clicked");
            }
        });

        enterGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add code to handle "Enter Game" button click
                JOptionPane.showMessageDialog(MainMenu.this, "Enter Game button clicked");
            }
        });

        viewHighScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch high scores from API
                List<String> highScores = fetchHighScores();
                // Update UI with high scores
                updateHighScoresUI(highScores);
            }
        });

        // Add buttons to panel
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(startGameButton);
        panel.add(enterGameButton);
        panel.add(viewHighScoresButton);

        highScoresLabel = new JLabel();
        panel.add(highScoresLabel);

        // Add panel to frame
        getContentPane().add(panel, BorderLayout.CENTER);

        // Center frame on screen
        setLocationRelativeTo(null);
    }

    private List<String> fetchHighScores() {
        List<String> highScores = new ArrayList<>();
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            // Create URL object
            URL url = new URL("http://localhost:8080/api/user/highscore");
            // Open connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // Get response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray highScoresArray = jsonResponse.getJSONArray("highScores");
                for (int i = 0; i < highScoresArray.length(); i++) {
                    JSONObject scoreObject = highScoresArray.getJSONObject(i);
                    int score = scoreObject.getInt("score");
                    String username = scoreObject.getString("username");
                    highScores.add(username + ": " + score);
                }
            } else {
                // Handle HTTP error
                System.out.println("Error: HTTP " + responseCode);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            // Close connections
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return highScores;
    }

    private void updateHighScoresUI(List<String> highScores) {
        StringBuilder sb = new StringBuilder("<html><b>High Scores:</b><br>");
        if (highScores != null && !highScores.isEmpty()) {
            for (String score : highScores) {
                sb.append(score).append("<br>");
            }
        } else {
            sb.append("No high scores available.");
        }
        sb.append("</html>");
        highScoresLabel.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
