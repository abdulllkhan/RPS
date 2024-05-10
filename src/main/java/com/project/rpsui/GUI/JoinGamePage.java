package com.project.rpsui.GUI;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinGamePage extends JFrame implements ActionListener {

    private JTextField sessionCodeField;
    private JButton joinButton;

    public InstanceInfo instanceInfoLocal;

    public JoinGamePage(InstanceInfo instanceInfo) {
    // public JoinGamePage() {
        this.instanceInfoLocal = instanceInfo;
        setTitle("Join Game");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel sessionCodeLabel = new JLabel("Enter Session Code:");
        inputPanel.add(sessionCodeLabel);

        sessionCodeField = new JTextField(20);
        inputPanel.add(sessionCodeField);

        joinButton = new JButton("Join Game");
        joinButton.addActionListener(this);
        inputPanel.add(joinButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == joinButton) {
            String sessionCode = sessionCodeField.getText().trim();
            if (!sessionCode.isEmpty()) {
                joinGame(instanceInfoLocal.getGamerId(), sessionCode); // Assuming user ID is 10
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a session code.");
            }
        }
    }

    private void joinGame(int userId, String sessionCode) {
    try {
        this.instanceInfoLocal.sessionCode  = sessionCode;
        URL url = new URL("http://localhost:8080/api/game/join");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        String requestBody = "{\"userId\": " + userId + ", \"sessionCode\": \"" + sessionCode + "\"}";
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.flush();

        int responseCode = connection.getResponseCode();

        BufferedReader reader;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            JOptionPane.showMessageDialog(null, "Game joined successfully!");

            // Call API to get game details
            URL gameDetailsUrl = new URL("http://localhost:8080/api/game/" + sessionCode);
            HttpURLConnection gameDetailsConnection = (HttpURLConnection) gameDetailsUrl.openConnection();
            gameDetailsConnection.setRequestMethod("GET");
            gameDetailsConnection.setRequestProperty("Content-Type", "application/json");

            int gameDetailsResponseCode = gameDetailsConnection.getResponseCode();

            BufferedReader gameDetailsReader;
            if (gameDetailsResponseCode == HttpURLConnection.HTTP_OK) {
                gameDetailsReader = new BufferedReader(new InputStreamReader(gameDetailsConnection.getInputStream()));
            } else {
                gameDetailsReader = new BufferedReader(new InputStreamReader(gameDetailsConnection.getErrorStream()));
            }

            StringBuilder gameDetailsResponse = new StringBuilder();
            String gameDetailsLine;
            while ((gameDetailsLine = gameDetailsReader.readLine()) != null) {
                gameDetailsResponse.append(gameDetailsLine);
            }
            gameDetailsReader.close();

            JSONObject gameDetailsJson = new JSONObject(gameDetailsResponse.toString());
            int gameId = gameDetailsJson.getInt("id");
            int user1Id = gameDetailsJson.getInt("user1Id");
            int user2Id = gameDetailsJson.getInt("user2Id");
            boolean isActive = gameDetailsJson.getBoolean("isActive");
            boolean isVacant = gameDetailsJson.getBoolean("isVacant");
            int winnerId = gameDetailsJson.getInt("winnerId");
            
            gameDetailsConnection.disconnect();

            instanceInfoLocal.setSessionId(gameId);
            instanceInfoLocal.setOpponentId(user1Id == userId ? user2Id : user1Id);

            dispose();
            new GamePage(instanceInfoLocal);

        } else {
            JOptionPane.showMessageDialog(null, "Error: Failed to join game.");
        }

        connection.disconnect();
    } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occurred while making API call.");
    }
    }

}
