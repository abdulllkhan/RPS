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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

// @Component
public class MainMenu extends JFrame {
    private JLabel highScoresLabel;

    // public MainMenu() {
    public MainMenu(InstanceInfo instanceInfo) {
        setTitle("Game Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton startGameButton = new JButton("Start Game");
        JButton enterGameButton = new JButton("Enter Game");
        JButton viewHighScoresButton = new JButton("View High Scores");

        setSize(500, 400);

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainMenu.this, "Start Game button clicked");
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
                new HighScoresPage(instanceInfo);
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

    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        //     MainMenu mainMenu = new MainMenu();
        //     mainMenu.setVisible(true);
        // });
    }
}
