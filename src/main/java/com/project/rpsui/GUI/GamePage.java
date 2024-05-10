package com.project.rpsui.GUI;

import javax.swing.*;

import org.json.JSONArray;
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

public class GamePage extends JFrame implements ActionListener {

    private JTextArea messagesArea;
    private JTextField messageField;
    private JButton rockButton, paperButton, scissorsButton, sendButton;

    public InstanceInfo instanceInfoLocal;

    public GamePage(InstanceInfo instanceInfo) {
        this.instanceInfoLocal = instanceInfo;
        setTitle("Rock Paper Scissors Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left panel for game options
        JPanel gamePanel = new JPanel(new GridLayout(3, 1));
        gamePanel.setPreferredSize(new Dimension(150, getHeight()));

        rockButton = new JButton("Rock");
        rockButton.addActionListener(this);
        gamePanel.add(rockButton);

        paperButton = new JButton("Paper");
        paperButton.addActionListener(this);
        gamePanel.add(paperButton);

        scissorsButton = new JButton("Scissors");
        scissorsButton.addActionListener(this);
        gamePanel.add(scissorsButton);

        mainPanel.add(gamePanel, BorderLayout.WEST);

        // Right panel for messages
        JPanel messagePanel = new JPanel(new BorderLayout());

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(messagesArea);
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel messageInputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageInputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        messagePanel.add(messageInputPanel, BorderLayout.SOUTH);

        mainPanel.add(messagePanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // everthing is breaking down here
        fetchMessages();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rockButton) {
            // Handle rock button action
        } else if (e.getSource() == paperButton) {
            // Handle paper button action
        } else if (e.getSource() == scissorsButton) {
            // Handle scissors button action
        } else if (e.getSource() == sendButton) {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(instanceInfoLocal.getUsername(), message, instanceInfoLocal.getSessionCode());
                messagesArea.append(instanceInfoLocal.getUsername() + ": " + message + "\n");
                messageField.setText("");
            }
        }
    }

    private void sendMessage(String sender, String message, String sessionCode) {
        try {
            URL url = new URL("http://localhost:8080/api/message/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            String requestBody = "{\"sender\": \"" + sender + "\", \"message\": \"" + message + "\", \"sessionCode\": \"" + sessionCode + "\"}";
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
                // String successMessage = jsonResponse.getString("message");
                // JOptionPane.showMessageDialog(null, successMessage);
            } else {
                JOptionPane.showMessageDialog(null, response.toString());
            }

            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while making API call.");
        }
    }

    private void fetchMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    URL url = new URL("http://localhost:8080/api/message/fetch/" + instanceInfoLocal.getSessionCode() + "?username=" + instanceInfoLocal.getUsername());
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
                    boolean hasMessages = jsonResponse.getBoolean("hasMessages");

                    if (hasMessages) {
                        int messageCount = jsonResponse.getInt("messageCount");
                        JSONArray messagesArray = jsonResponse.getJSONArray("messages");
                        for (int i = 0; i < messageCount; i++) {
                            JSONObject messageObject = messagesArray.getJSONObject(i);
                            String message = messageObject.getString("message");
                            String sender = messageObject.getString("sender");
                            long createdAt = messageObject.getLong("createdAt");
                            displayMessage(sender, message, createdAt);
                        }
                    }

                    Thread.sleep(1000); // Fetch new messages every second
                }
            } catch (IOException | JSONException | InterruptedException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while fetching messages.");
            }
        }).start();
    }

    private void displayMessage(String sender, String message, long createdAt) {
        SwingUtilities.invokeLater(() -> {
            messagesArea.append(sender + ": " + message + "\n");
        });
    }

    // public static void main(String[] args) {
    //     // SwingUtilities.invokeLater(() -> new GamePage());
    // }
}
