package com.project.rpsui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// @Component
public class MainMenu extends JFrame {
    private JLabel highScoresLabel;

    private InstanceInfo instanceInfoLocal;

    // public MainMenu() {
    public MainMenu(InstanceInfo instanceInfo) {
        this.instanceInfoLocal = instanceInfo;
        setTitle("Game Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton startGameButton = new JButton("Start Game");
        JButton enterGameButton = new JButton("Enter Game");
        JButton viewHighScoresButton = new JButton("View High Scores");

        setSize(500, 400);

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(MainMenu.this, "Start Game button clicked");
                startNewGame(instanceInfo.getGamerId()); // Assuming you have a method to get the user ID
            }
        });

        enterGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new JoinGamePage(instanceInfo);
            }
        });

        viewHighScoresButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                dispose();
                new HighScoresPage(instanceInfoLocal);
            }
        });

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(startGameButton);
        panel.add(enterGameButton);
        panel.add(viewHighScoresButton);

        highScoresLabel = new JLabel();
        panel.add(highScoresLabel);

        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);

        setLocationRelativeTo(null);
    }

    private void startNewGame(int userId) {
    try {
        URL url = new URL("http://localhost:8080/api/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        String requestBody = "{\"userId\": " + userId + "}";
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
            JSONObject jsonResponse = new JSONObject(response.toString());
            String roomMessage = jsonResponse.getString("message");
            String sessionCode = jsonResponse.getString("sessionCode");
            JOptionPane.showMessageDialog(MainMenu.this, roomMessage + "\nSession Code: " + sessionCode);

            instanceInfoLocal.setSessionCode(sessionCode);

            dispose();
            // new GamePage(instanceInfoLocal);
            new WaitingRoomPage(instanceInfoLocal);

        } else {
            JOptionPane.showMessageDialog(MainMenu.this, response.toString());
        }

        connection.disconnect();
    } catch (IOException | JSONException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(MainMenu.this, "Error occurred while making API call.");
    }
}

    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        //     MainMenu mainMenu = new MainMenu();
        //     mainMenu.setVisible(true);
        // });
    }
}
