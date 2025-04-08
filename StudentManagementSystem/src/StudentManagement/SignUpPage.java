package StudentManagement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpPage extends Application {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = "admin@123";  

    @Override
    public void start(Stage primaryStage) {
        Label signUpLabel = new Label("Create Account");
        signUpLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        Label signUpAsNewFacultyLabel = new Label("Sign up as new faculty");
        signUpAsNewFacultyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        // Full Name Field
        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefWidth(250);
        nameField.setMaxWidth(350);

        // Username Field
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        usernameField.setMaxWidth(350);

        // Password Field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(350);

        // Logo Image
        Image logoImage = new Image(getClass().getResource("/StudentManagement/logo1.png").toExternalForm());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(100);
        logoImageView.setFitWidth(100);

        // Sign Up Button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> {
            String fullName = nameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert(AlertType.ERROR, "Sign-Up Failed", "Please fill in all fields!");
            } else {
                if (isUsernameAvailable(username)) {
                    saveUserDetails(fullName, username, password);
                    showAlert(AlertType.INFORMATION, "Sign-Up Successful", "Your account has been created successfully.");
                } else {
                    // Username already exists, alert will be shown from the isUsernameAvailable method
                }
            }
        });

        // Back to Login Button
        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        // Exit Button
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> primaryStage.close());

        VBox vbox = new VBox(20, signUpLabel, signUpAsNewFacultyLabel, logoImageView, nameLabel, nameField, userLabel, usernameField, passwordLabel, passwordField, signUpButton, backToLoginButton, exitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #FFFFE0;");

        Scene scene = new Scene(vbox, 1920, 1080);
        primaryStage.setTitle("Student Management System - Sign Up");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to save user details to the database
    private void saveUserDetails(String fullName, String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO faculty (name, username, password) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, fullName);
                statement.setString(2, username);
                statement.setString(3, password);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while saving your details.");
        }
    }

    // Check if the username is available
    private boolean isUsernameAvailable(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM faculty WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // If username exists, show an alert
                    showAlert(AlertType.ERROR, "Sign-Up Failed", "User with this username already exists!");
                    return false;  // Username already exists, return false
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while checking the username.");
        }
        return true;  // Username is available
    }

    public static void main(String[] args) {
        launch(args);
    }
}
