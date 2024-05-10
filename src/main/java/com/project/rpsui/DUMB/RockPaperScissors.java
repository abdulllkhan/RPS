package com.project.rpsui.DUMB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RockPaperScissors extends JFrame implements ActionListener {
    private JButton rockButton, paperButton, scissorsButton;
    private JLabel resultLabel;

    public RockPaperScissors() {
        setTitle("Rock Paper Scissors");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        rockButton = new JButton("Rock");
        rockButton.addActionListener(this);
        panel.add(rockButton);

        paperButton = new JButton("Paper");
        paperButton.addActionListener(this);
        panel.add(paperButton);

        scissorsButton = new JButton("Scissors");
        scissorsButton.addActionListener(this);
        panel.add(scissorsButton);

        resultLabel = new JLabel("Choose your move!");
        panel.add(resultLabel);

        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String playerMove = "";
        String computerMove = "";
        
        if (e.getSource() == rockButton) {
            playerMove = "Rock";
        } else if (e.getSource() == paperButton) {
            playerMove = "Paper";
        } else if (e.getSource() == scissorsButton) {
            playerMove = "Scissors";
        }

        Random rand = new Random();
        int computerMoveIndex = rand.nextInt(3);
        switch (computerMoveIndex) {
            case 0:
                computerMove = "Rock";
                break;
            case 1:
                computerMove = "Paper";
                break;
            case 2:
                computerMove = "Scissors";
                break;
        }

        String result = determineWinner(playerMove, computerMove);
        resultLabel.setText("Player chose " + playerMove + ", Computer chose " + computerMove + ". " + result);
    }

    private String determineWinner(String playerMove, String computerMove) {
        if (playerMove.equals(computerMove)) {
            return "It's a tie!";
        } else if ((playerMove.equals("Rock") && computerMove.equals("Scissors")) ||
                (playerMove.equals("Paper") && computerMove.equals("Rock")) ||
                (playerMove.equals("Scissors") && computerMove.equals("Paper"))) {
            return "Player wins!";
        } else {
            return "Computer wins!";
        }
    }

    public static void main(String[] args) {
        new RockPaperScissors();
    }
}
