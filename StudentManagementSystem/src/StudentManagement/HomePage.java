package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class HomePage extends Application {

    private String facultyName;

    public HomePage(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public void start(Stage primaryStage) {
        // Fetch faculty name dynamically from database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudentManagement", "username", "enteryourpassword")) {
            String query = "SELECT name FROM faculty WHERE id = ?";  // Adjust query as per your schema
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 1);  // Example: Using faculty ID 1, adjust as needed
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                facultyName = resultSet.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Welcome Label
        Label welcomeLabel = new Label("Welcome, " + facultyName);
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Logo Image
        Image logoImage = new Image(getClass().getResource("/StudentManagement/logo1.png").toExternalForm());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(100);
        logoImageView.setFitWidth(100);

        // Label for "Student Management System" below the logo
        Label systemLabel = new Label("Student Management System");
        systemLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Buttons
        Button attendanceButton = new Button("Student Attendance");
        Button performanceButton = new Button("Student Performance");
        Button addButton = new Button("Add");
        Button exitButton = new Button("Exit");
        Button signOutButton = new Button("Sign Out");

        // Set styles for buttons
        setButtonStyle(attendanceButton);
        setButtonStyle(performanceButton);
        setButtonStyle(addButton);
        setButtonStyle(exitButton);

        // Sign Out Button (Top-right corner)
        signOutButton.setOnAction(e -> showSignOutConfirmation(primaryStage));

        // Exit Button
        exitButton.setOnAction(e -> primaryStage.close());

        // Add Button (Redirect to HomePage2)
        addButton.setOnAction(e -> {
            HomePage2 homePage2 = new HomePage2(facultyName);  // Pass faculty name dynamically
            homePage2.start(primaryStage);  // Navigate to HomePage2
        });

        // Student Attendance Button
        attendanceButton.setOnAction(e -> {
            StudentAttendance studentAttendance = new StudentAttendance();
            studentAttendance.start(primaryStage);
        });

        // Student Performance Button
        performanceButton.setOnAction(e -> {
            StudentPerformance studentPerformance = new StudentPerformance();
            studentPerformance.start(primaryStage);
        });
        
        

        // Set up GridPane layout for button arrangement
        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);  // Vertical gap between buttons
        gridPane.setHgap(20);  // Horizontal gap between buttons
        gridPane.setAlignment(Pos.CENTER);

        // Adding buttons to the grid
        gridPane.add(attendanceButton, 0, 0);
        gridPane.add(performanceButton, 1, 0);
        gridPane.add(addButton, 0, 1);
        gridPane.add(exitButton, 1, 1);

        // Adding the Welcome label, Logo, "Student Management System" label, and Sign Out button at the top
        VBox topPane = new VBox(10);
        topPane.setAlignment(Pos.TOP_CENTER);
        topPane.getChildren().addAll(logoImageView, systemLabel, welcomeLabel, signOutButton);

        // Adjust the vertical spacing and set padding for better alignment
        topPane.setStyle("-fx-padding: 30 0 0 0;");

        // Setting up the root layout (VBox for the top and grid for the buttons)
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FFFFE0;"); // Yellow background
        root.getChildren().addAll(topPane, gridPane);

        // Scene setup with resolution 1000x700
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Home Page - Student Management System");
        primaryStage.setScene(scene);

        // Center the stage and allow resizing
        primaryStage.setResizable(true);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();  // Center the stage on screen

        primaryStage.show();
    }

    private void setButtonStyle(Button button) {
        button.setStyle("-fx-pref-width: 180px; -fx-pref-height: 120px; -fx-font-size: 12px; -fx-font-weight: bold;");
    }

    private void showSignOutConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Signed Out");
        alert.setHeaderText(null);
        alert.setContentText("You have successfully signed out.");
        alert.showAndWait();

        LoginPage loginPage = new LoginPage();
        loginPage.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
