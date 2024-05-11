package com.project.rpsui.GUI;

import javax.swing.*;

import org.json.JSONException;
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

public class GameEndPage extends JFrame implements ActionListener{

    private JLabel winnerLabel;

    private InstanceInfo instanceInfoLocal;

    public GameEndPage(InstanceInfo instanceInfo) {

        setTitle("Game Over");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        winnerLabel = new JLabel("Winner: Fetching winner...");
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(winnerLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Return to Main Menu");
        // closeButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         endGame(instanceInfo.getSessionCode());
        //         instanceInfoLocal.setSessionCode(null);
        //         instanceInfoLocal.setOpponentId(null);
        //         instanceInfoLocal.setOpponentUsername(null);
        //         instanceInfoLocal.setSessionId(null);
        //         dispose();
        //         new MainMenu(instanceInfoLocal); // Open the Main Menu
        //     }
        // });
        mainPanel.add(closeButton, BorderLayout.SOUTH);

        add(mainPanel);
        closeButton.addActionListener(this);
        setLocationRelativeTo(null);
        setVisible(true);

        // Fetch the winner information
        fetchWinner(instanceInfo.getSessionCode());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Return to Main Menu")) {
            endGame(instanceInfoLocal.getSessionCode());
            instanceInfoLocal.setSessionCode(null);
            instanceInfoLocal.setOpponentId(null);
            instanceInfoLocal.setOpponentUsername(null);
            instanceInfoLocal.setSessionId(null);
            dispose();
            new MainMenu(instanceInfoLocal); // Open the Main Menu
        }
        
    }

    private void endGame(String sessionCode) {
        // new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/api/game/end");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setDoOutput(true);
                String requestBody = "{\"sessionCode\": \"" + sessionCode + "\"}";
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Game ended successfully!");
                } else {
                    System.out.println("Error: Failed to end game.");
                }

                connection.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error occurred while making API call to end game.");
            }
        // }).start();
    }
    
    private void fetchWinner(String sessionCode) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/api/game/winner/" + sessionCode);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

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
                    // Parse JSON response to get winner's name
                    String winnerName = parseWinnerResponse(response.toString());
                    updateWinnerLabel(winnerName);
                } else {
                    updateWinnerLabel("Failed to fetch winner");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                updateWinnerLabel("Error occurred while fetching winner");
            }
        }).start();
    }

    private String parseWinnerResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getString("winnerName");
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing winner response";
        }
    }

    private void updateWinnerLabel(String winnerName) {
        SwingUtilities.invokeLater(() -> {
            winnerLabel.setText("Winner: " + winnerName);
        });
    }

    public static void main(String[] args) {
        // Example usage:
        // Display the GameEndPage with the winner information
        // SwingUtilities.invokeLater(() -> new GameEndPage("qkrhzdCpJP"));s
    }

}
