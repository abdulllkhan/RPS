package com.project.rpsui.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitingRoomPage extends JFrame {

    private InstanceInfo instanceInfo;
    private JTextField sessionCodeField; 

    public WaitingRoomPage(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;

        setTitle("Waiting Room");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel waitingLabel = new JLabel("Share the following code with your opponent. Waiting for opponent...");
        add(waitingLabel, BorderLayout.NORTH);

        sessionCodeField = new JTextField(instanceInfo.getSessionCode());
        sessionCodeField.setEditable(false); 
        add(sessionCodeField, BorderLayout.CENTER);

        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> {
            sessionCodeField.selectAll();
            sessionCodeField.copy();
        });
        add(copyButton, BorderLayout.SOUTH);

        setLocationRelativeTo(null);

        setVisible(true);

        pollRoomDetails();
    }

    private void pollRoomDetails() {
        new Thread(() -> {
            try {
                while (true) {
                    URL url = new URL("http://localhost:8080/api/game/" + instanceInfo.getSessionCode());
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

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    boolean isVacant = jsonResponse.getBoolean("isVacant");

                    if (!isVacant) {
                        dispose();
                        Integer userId1 = jsonResponse.getInt("user1Id");
                        Integer userId2 = jsonResponse.getInt("user2Id");
                        if (userId1 == instanceInfo.getGamerId()) {
                            instanceInfo.setOpponentId(userId2);
                        } else {
                            instanceInfo.setOpponentId(userId1);
                        }
                        new GamePage(instanceInfo);
                        break;
                    }

                    Thread.sleep(3000); // Poll every second
                }
            } catch (IOException | JSONException | InterruptedException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while fetching room details." + ex.getMessage());
            }
        }).start();
    }
}
