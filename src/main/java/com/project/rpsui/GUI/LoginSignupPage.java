package com.project.rpsui.GUI;
import javax.swing.*;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject; 

// @Component
public class LoginSignupPage extends JFrame implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    private InstanceInfo instanceInfoLocal;

    public LoginSignupPage(InstanceInfo instanceInfo) {
    // public LoginSignupPage() {
        // setTitle("Login / Signup");
        this.instanceInfoLocal = instanceInfo;
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        signupButton = new JButton("Signup");
        signupButton.addActionListener(this);
        panel.add(signupButton);

        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                login(username, password);
            } catch (NoSuchAlgorithmException | RuntimeException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == signupButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                signup(username, password);
            } catch (NoSuchAlgorithmException | RuntimeException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }


    private void login(String username, String password) throws NoSuchAlgorithmException, RuntimeException {
        try {
            URL url = new URL("http://localhost:8080/api/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // converting password to SHA-256 hash
            
            connection.setDoOutput(true);
            // password = SHA256.getSHA256(password);
            String requestBody = "{\"username\": \"" + username + "\", \"hashedPassword\": \"" + password + "\"}";
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
            
            JOptionPane.showMessageDialog(null, response.toString());
            
            connection.disconnect();

            // if (responseCode == HttpURLConnection.HTTP_OK) {
            //     ////////////////////////////
            //     ////////////////////////////
            //     ////////////////////////////
            //     JSONObject jsonResponse = new JSONObject(response.toString());
            //     Integer userId = jsonResponse.getInt("id");
    
            //     instanceInfoLocal.gamerId = userId;

            //     dispose();
            //     new MainMenu(instanceInfoLocal);
                
            // }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONObject jsonResponse = new JSONObject(response.toString());
                int userId = jsonResponse.getInt("id");
    
                instanceInfoLocal.gamerId = userId;
    
                URL gamerDetailsUrl = new URL("http://localhost:8080/api/user/" + userId);
                HttpURLConnection gamerDetailsConnection = (HttpURLConnection) gamerDetailsUrl.openConnection();
                gamerDetailsConnection.setRequestMethod("GET");
                gamerDetailsConnection.setRequestProperty("Content-Type", "application/json");
    
                int gamerDetailsResponseCode = gamerDetailsConnection.getResponseCode();
    
                BufferedReader gamerDetailsReader;
                if (gamerDetailsResponseCode == HttpURLConnection.HTTP_OK) {
                    gamerDetailsReader = new BufferedReader(new InputStreamReader(gamerDetailsConnection.getInputStream()));
                } else {
                    gamerDetailsReader = new BufferedReader(new InputStreamReader(gamerDetailsConnection.getErrorStream()));
                }
    
                StringBuilder gamerDetailsResponse = new StringBuilder();
                String gamerDetailsLine;
                while ((gamerDetailsLine = gamerDetailsReader.readLine()) != null) {
                    gamerDetailsResponse.append(gamerDetailsLine);
                }
                gamerDetailsReader.close();
    
                JSONObject gamerDetailsJson = new JSONObject(gamerDetailsResponse.toString());
                String gamerName = gamerDetailsJson.getString("username");
                long createdAt = gamerDetailsJson.getLong("createdAt");

                instanceInfoLocal.username = gamerName;
                instanceInfoLocal.createdAt = createdAt;

                gamerDetailsConnection.disconnect();

                dispose();
                new MainMenu(instanceInfoLocal);
                
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while making API call.");
        }
    }

    private void signup(String username, String password) throws NoSuchAlgorithmException, RuntimeException {
        try {
            URL url = new URL("http://localhost:8080/api/user");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
    
            connection.setDoOutput(true);
            // password = SHA256.getSHA256(password);
            String requestBody = "{\"username\": \"" + username + "\", \"hashedPassword\": \"" + password + "\"}";
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
    
            JOptionPane.showMessageDialog(null, response.toString());
    
            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while making API call.");
        }
    }
    

    public static void main(String[] args) {
        // new LoginSignupPage(new InstanceInfo());
    }
}
