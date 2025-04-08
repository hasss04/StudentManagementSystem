package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StudentPerformance extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Title label
        Label titleLabel = new Label("Student Performance");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 40px;");

        // Buttons for "Enter Student Marks" and "Print Student Marks"
        Button enterMarksButton = new Button("Enter Student Marks");
        Button printInfoButton = new Button("Print Student Marks");

        // Back Button to return to HomePage
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ffcc00;");
        backButton.setOnAction(e -> openHomePage(primaryStage));

        // Set actions for the buttons
        enterMarksButton.setOnAction(e -> openEnterMarksPage());
        printInfoButton.setOnAction(e -> openStudentInfoPage());

        // Set up layout
        VBox vbox = new VBox(20, titleLabel, enterMarksButton, printInfoButton, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #FFFFE0;");

        Scene scene = new Scene(vbox, 1920, 1080);
        primaryStage.setTitle("Student Performance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to open EnterMarks page (for entering student marks)
    private void openEnterMarksPage() {
        EnterMark enterMarksPage = new EnterMark();
        Stage newStage = new Stage();
        enterMarksPage.start(newStage);
    }

    // Method to open StudentInfo page (for printing student marks)
    private void openStudentInfoPage() {
        Studentinfo studentInfoPage = new Studentinfo();
        Stage newStage = new Stage();
        studentInfoPage.start(newStage);
    }

    // Method to go back to HomePage
    private void openHomePage(Stage currentStage) {
        HomePage homePage = new HomePage("Faculty Name");
        Stage homeStage = new Stage();
        homePage.start(homeStage);
        currentStage.close(); // Close the current StudentPerformance window
    }

    public static void main(String[] args) {
        launch(args);
    }
}
