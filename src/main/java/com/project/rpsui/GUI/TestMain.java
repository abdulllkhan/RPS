package com.project.rpsui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TestMain extends JFrame {
    private JPanel mainMenuPanel;

    public TestMain() {
        setTitle("Main Menu");
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
                JOptionPane.showMessageDialog(TestMain.this, "Start Game button clicked");
            }
        });

        enterGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add code to handle "Enter Game" button click
                JOptionPane.showMessageDialog(TestMain.this, "Enter Game button clicked");
            }
        });

        viewHighScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display high scores
                displayHighScores();
            }
        });

        // Add buttons to panel
        mainMenuPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainMenuPanel.add(startGameButton);
        mainMenuPanel.add(enterGameButton);
        mainMenuPanel.add(viewHighScoresButton);

        // Add panel to frame
        getContentPane().add(mainMenuPanel, BorderLayout.CENTER);

        // Center frame on screen
        setLocationRelativeTo(null);
    }

    private void displayHighScores() {
        // Fetch high scores from HighScoreFetcher
        List<String> highScores = HighScoreFetcher.fetchHighScores();

        // Clear the content pane
        mainMenuPanel.setVisible(false);

        // Create a new panel to display high scores
        JPanel highScoresPanel = new JPanel();
        highScoresPanel.setLayout(new BoxLayout(highScoresPanel, BoxLayout.Y_AXIS));

        // Add high scores to the panel
        for (String score : highScores) {
            JLabel scoreLabel = new JLabel(score);
            highScoresPanel.add(scoreLabel);
        }

        // Add a button to return to the main menu
        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Re-display the main menu
                mainMenuPanel.setVisible(true);
                highScoresPanel.setVisible(false);
            }
        });
        highScoresPanel.add(returnButton);

        // Add the high scores panel to the content pane
        getContentPane().add(highScoresPanel);
        highScoresPanel.setVisible(true);

        // Refresh the frame
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TestMain mainMenu = new TestMain();
            mainMenu.setVisible(true);
        });
    }
}
