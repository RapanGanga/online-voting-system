import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OnlineVotingSystem {
    private Map<String, String> userCredentials;
    private Map<String, String> userOTP;
    private Frame loginFrame;
    private Frame voteFrame;
    private TextField usernameField;
    private TextField passwordField;
    private TextField otpField;
    private Choice candidateChoice;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "harshitha123";

    public OnlineVotingSystem() {
        userCredentials = new HashMap<>();
        userOTP = new HashMap<>();
        initializeUserCredentials();
        initializeLoginFrame();
        initializeVoteFrame();
    }

    private void initializeUserCredentials() {
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");
        userCredentials.put("user3", "password3");
        userCredentials.put("user4", "password4");
        userCredentials.put("user5", "password5");
    }

    private void initializeLoginFrame() {
        loginFrame = new Frame("Online Voting System - Login");
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(null);

        Label usernameLabel = new Label("Username:");
        usernameLabel.setBounds(30, 30, 80, 30);
        loginFrame.add(usernameLabel);

        usernameField = new TextField();
        usernameField.setBounds(150, 30, 150, 30);
        loginFrame.add(usernameField);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setBounds(30, 60, 80, 30);
        loginFrame.add(passwordLabel);

        passwordField = new TextField();
        passwordField.setBounds(150, 60, 150, 30);
        passwordField.setEchoChar('*');
        loginFrame.add(passwordField);

        Button loginButton = new Button("Login");
        loginButton.setBounds(150, 100, 80, 30);
        loginButton.addActionListener(e -> loginButtonClicked());
        loginFrame.add(loginButton);

        loginFrame.setVisible(true);
    }

    private void initializeVoteFrame() {
        voteFrame = new Frame("Online Voting System - Vote");
        voteFrame.setSize(400, 300);
        voteFrame.setLayout(null);

        Label otpLabel = new Label("Enter OTP:");
        otpLabel.setBounds(20, 40, 80, 25);
        voteFrame.add(otpLabel);

        otpField = new TextField();
        otpField.setBounds(120, 40, 150, 25);
        voteFrame.add(otpField);

        Label candidateLabel = new Label("Select Candidate:");
        candidateLabel.setBounds(20, 70, 120, 25);
        voteFrame.add(candidateLabel);

        candidateChoice = new Choice();
        candidateChoice.setBounds(150, 70, 150, 25);
        candidateChoice.add("Candidate 1");
        candidateChoice.add("Candidate 2");
        candidateChoice.add("Candidate 3");
        voteFrame.add(candidateChoice);

        Button voteButton = new Button("Vote");
        voteButton.setBounds(150, 100, 80, 25);
        voteButton.addActionListener(e -> voteButtonClicked());
        voteFrame.add(voteButton);

        voteFrame.setVisible(false);
    }

    private void loginButtonClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            String otp = generateOTP();
            sendOTP(username, otp);
            userOTP.put(username, otp);

            loginFrame.setVisible(false);
            voteFrame.setVisible(true);
        } else {
            System.out.println("Invalid credentials");
        }
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOTP(String username, String otp) {
        System.out.println("OTP sent to " + username + ": " + otp);
    }

    private void voteButtonClicked() {
        String username = usernameField.getText();
        String otp = otpField.getText();
        int candidateIndex = candidateChoice.getSelectedIndex() + 1;

        if (userOTP.containsKey(username) && userOTP.get(username).equals(otp)) {
            storeVoteInDatabase(username, candidateIndex);
            System.out.println("Vote cast successfully!");
            voteFrame.setVisible(false);
        } else {
            System.out.println("Invalid OTP. Vote not cast.");
        }
    }

    private void storeVoteInDatabase(String username, int candidateIndex) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO voting_results (username, candidate_index) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, candidateIndex);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to store vote in the database");
        }
    }

    public static void main(String[] args) {
        new OnlineVotingSystem();
    }
}
