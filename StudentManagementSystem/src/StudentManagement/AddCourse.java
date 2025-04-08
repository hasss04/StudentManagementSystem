package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class AddCourse extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private String facultyName;

    // Constructor accepting faculty name
    public AddCourse(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Add Course");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        TextField courseTextField = new TextField();
        TextField branchTextField = new TextField();
        courseTextField.setPromptText("Enter Course");
        branchTextField.setPromptText("Enter Branch");

        Button addButton = new Button("Add Course");
        Button backButton = new Button("Back");

        addButton.setOnAction(e -> {
            String course = courseTextField.getText();
            String branch = branchTextField.getText();

            if (course.isEmpty() || branch.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill all fields.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String insertQuery = "INSERT INTO courses (course, branch) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertQuery);
                stmt.setString(1, course);
                stmt.setString(2, branch);
                stmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully!");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add course.");
                ex.printStackTrace();
            }
        });

        backButton.setOnAction(e -> {
            HomePage2 homePage = new HomePage2(facultyName);
            homePage.start(primaryStage);
        });

        VBox layout = new VBox(20, titleLabel, courseTextField, branchTextField, addButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setTitle("Add Course");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
