import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RockPaperScissorServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS_PER_SESSION = 2;

    private List<Session> sessions;
    private JTextArea logTextArea;

    public RockPaperScissorServer() {
        sessions = new ArrayList<>();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Rock Paper Scissors Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logTextArea = new JTextArea(10, 30);
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logTextArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        RockPaperScissorServer server = new RockPaperScissorServer();
        server.start();
    }

    private void start() {
        log("Server started. Waiting for clients...");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log("Client connected: " + clientSocket);

                // Try to assign client to an existing session
                boolean assignedToSession = false;
                for (Session session : sessions) {
                    if (!session.isFull()) {
                        session.addPlayer(clientSocket);
                        assignedToSession = true;
                        break;
                    }
                }

                // If not assigned to an existing session, create a new session
                if (!assignedToSession) {
                    Session newSession = new Session();
                    newSession.addPlayer(clientSocket);
                    sessions.add(newSession);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Session implements Runnable {
        private static final int MAX_PLAYERS_PER_SESSION = 2;

        private Socket[] players;
        private PrintWriter[] outputs;
        private Scanner[] inputs;

        public Session() {
            players = new Socket[MAX_PLAYERS_PER_SESSION];
            outputs = new PrintWriter[MAX_PLAYERS_PER_SESSION];
            inputs = new Scanner[MAX_PLAYERS_PER_SESSION];
        }

        public boolean isFull() {
            return players[MAX_PLAYERS_PER_SESSION - 1] != null;
        }

        public void addPlayer(Socket playerSocket) {
            for (int i = 0; i < MAX_PLAYERS_PER_SESSION; i++) {
                if (players[i] == null) {
                    players[i] = playerSocket;
                    try {
                        outputs[i] = new PrintWriter(playerSocket.getOutputStream(), true);
                        inputs[i] = new Scanner(playerSocket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if (isFull()) {
                new Thread(this).start(); // Start the session in a new thread
            }
        }

        @Override
        public void run() {
            while (true) {
                // Wait for both players to make moves
                String[] playerMoves = new String[MAX_PLAYERS_PER_SESSION];
                for (int i = 0; i < MAX_PLAYERS_PER_SESSION; i++) {
                    playerMoves[i] = inputs[i].nextLine();
                    log("Player " + (i + 1) + " move: " + playerMoves[i]);
                }

                // Determine the winner
                String result;
                if (playerMoves[0].equals(playerMoves[1])) {
                    result = "draw";
                } else if (playerMoves[0].equals("rock") && playerMoves[1].equals("scissors") ||
                        playerMoves[0].equals("paper") && playerMoves[1].equals("rock") ||
                        playerMoves[0].equals("scissors") && playerMoves[1].equals("paper")) {
                    result = "Player 1 wins";
                } else {
                    result = "Player 2 wins";
                }

                // Send game result to players
                for (int i = 0; i < MAX_PLAYERS_PER_SESSION; i++) {
                    outputs[i].println(result);
                }
            }
        }
    }
}
