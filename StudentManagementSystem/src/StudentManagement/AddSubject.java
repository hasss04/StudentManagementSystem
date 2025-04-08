package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class AddSubject extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin@123";
    private String facultyName;

    // Constructor accepting faculty name
    public AddSubject(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Add Subject");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        TextField courseTextField = new TextField();
        TextField branchTextField = new TextField();
        TextField subjectCodeTextField = new TextField();
        TextField subjectNameTextField = new TextField();

        courseTextField.setPromptText("Enter Course");
        branchTextField.setPromptText("Enter Branch");
        subjectCodeTextField.setPromptText("Enter Subject Code");
        subjectNameTextField.setPromptText("Enter Subject Name");

        Button addButton = new Button("Add Subject");
        Button backButton = new Button("Back");

        addButton.setOnAction(e -> {
            String course = courseTextField.getText();
            String branch = branchTextField.getText();
            String subjectCode = subjectCodeTextField.getText();
            String subjectName = subjectNameTextField.getText();

            if (course.isEmpty() || branch.isEmpty() || subjectCode.isEmpty() || subjectName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill all fields.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String insertQuery = "INSERT INTO subjects (course, branch, subject_code, subject_name) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertQuery);
                stmt.setString(1, course);
                stmt.setString(2, branch);
                stmt.setString(3, subjectCode);
                stmt.setString(4, subjectName);
                stmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Subject added successfully!");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add subject.");
                ex.printStackTrace();
            }
        });

        backButton.setOnAction(e -> {
            HomePage2 homePage = new HomePage2(facultyName);
            homePage.start(primaryStage);
        });

        VBox layout = new VBox(20, titleLabel, courseTextField, branchTextField, subjectCodeTextField, subjectNameTextField, addButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setTitle("Add Subject");
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
