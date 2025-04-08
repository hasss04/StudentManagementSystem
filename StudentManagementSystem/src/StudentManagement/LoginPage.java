package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class LoginPage extends Application {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(true);  // Allow resizing to access minimize, maximize, and close options

        // Title label
        Label titleLabel = new Label("Student Management System");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 35px;");

        // Welcome label
        Label welcomeLabel = new Label("Welcome!");
        welcomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        // Faculty login label
        Label loginForFacultyLabel = new Label("Faculty Login");
        loginForFacultyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        // Logo Image
        Image logoImage = new Image(getClass().getResource("/StudentManagement/logo1.png").toExternalForm());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(100);
        logoImageView.setFitWidth(100);

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

        // Login Button with database validation
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(username, password)) {
                String facultyName = getFacultyName(username);
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + facultyName);
                HomePage homePage = new HomePage(facultyName);
                homePage.start(primaryStage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
            }
        });

        // Sign Up Button
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> {
            SignUpPage signUpPage = new SignUpPage();
            signUpPage.start(primaryStage);
        });

        // Exit Button
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> primaryStage.close());

        // Layout setup
        VBox vbox = new VBox(20, titleLabel, welcomeLabel, loginForFacultyLabel, logoImageView, userLabel, usernameField, passwordLabel, passwordField, loginButton, signUpButton, exitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #FFFFE0;");

        // Scene setup with updated resolution
        Scene scene = new Scene(vbox, 1000, 700);
        primaryStage.setTitle("Student Management System - Login");
        primaryStage.setScene(scene);

        // Center the stage on the screen
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    private boolean validateLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM faculty WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error connecting to the database.");
        }
        return false;
    }

    private String getFacultyName(String username) {
        String facultyName = "";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT name FROM faculty WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    facultyName = resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facultyName;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
