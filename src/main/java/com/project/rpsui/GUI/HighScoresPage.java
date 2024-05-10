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
import org.json.JSONArray;
import org.json.JSONObject;

public class HighScoresPage extends JFrame implements ActionListener {
    private JButton returnButton;

    public HighScoresPage() {
        setTitle("Top 10 High Scores");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new GridLayout(11, 2));

        JLabel headerLabel = new JLabel("Username");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoresPanel.add(headerLabel);

        JLabel headerLabel2 = new JLabel("Score");
        headerLabel2.setFont(new Font("Arial", Font.BOLD, 16));
        scoresPanel.add(headerLabel2);

        try {
            // Send HTTP GET request to the API
            URL url = new URL("http://localhost:8080/api/user/highscore?vv=null");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray highScores = jsonObject.getJSONArray("highScores");

            // Display top 10 scores
            for (int i = 0; i < Math.min(highScores.length(), 10); i++) {
                JSONObject scoreObj = highScores.getJSONObject(i);
                String username = scoreObj.getString("username");
                int score = scoreObj.getInt("score");
                JLabel usernameLabel = new JLabel(username);
                scoresPanel.add(usernameLabel);
                JLabel scoreLabel = new JLabel(Integer.toString(score));
                scoresPanel.add(scoreLabel);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching high scores.");
        }

        panel.add(scoresPanel, BorderLayout.CENTER);

        returnButton = new JButton("Return to Main Menu");
        returnButton.addActionListener(this);
        panel.add(returnButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            // Close the high scores page
            dispose();
            new MainMenu();
            // Open the main menu
        }
    }

    public static void main(String[] args) {
        new HighScoresPage();
    }
}
