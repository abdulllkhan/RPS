import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RockPaperScissorGUI {

    private String playerMove;
    private JLabel resultLabel;
    private PrintWriter out; // For sending moves to the server
    private Scanner in; // For receiving results from the server

    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RockPaperScissorGUI gui = new RockPaperScissorGUI();
            gui.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Rock Paper Scissors");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem connectMenuItem = new JMenuItem("Connect/Start Game");
        connectMenuItem.addActionListener(e -> connectToServer());
        optionsMenu.add(connectMenuItem);
        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);
       

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel playerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Player"));
        rockButton = new JButton("Rock");
        rockButton.addActionListener(e -> handleButton("rock"));
        playerPanel.add(rockButton);
        paperButton = new JButton("Paper");
        paperButton.addActionListener(e -> handleButton("paper"));
        playerPanel.add(paperButton);
        scissorsButton = new JButton("Scissors");
        scissorsButton.addActionListener(e -> handleButton("scissors"));
        playerPanel.add(scissorsButton);

        resultLabel = new JLabel();
        panel.add(playerPanel, BorderLayout.WEST);
        panel.add(resultLabel, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345); // Replace "localhost" with the server's IP address if needed
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            System.out.println("Connected to server");
            // Start a new thread to continuously listen for results from the server
            new Thread(this::listenForResults).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForResults() {
        while (true) {
            if (in.hasNextLine()) {
                String result = in.nextLine();
                // Update the result label with the received result from the server
                SwingUtilities.invokeLater(() -> resultLabel.setText(result));
                // Re-enable the buttons after receiving the result
                rockButton.setEnabled(true);
                paperButton.setEnabled(true);
                scissorsButton.setEnabled(true);
            }
        }
    }


    private void handleButton(String move) {
        if (out != null) {
            out.println(move); // Send the player's move to the server
            // Disable buttons after a move is made
            rockButton.setEnabled(false);
            paperButton.setEnabled(false);
            scissorsButton.setEnabled(false);
        } else {
            // Not connected to the server
            JOptionPane.showMessageDialog(null, "Please connect to the server first", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}