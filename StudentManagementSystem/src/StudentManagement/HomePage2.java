package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage2 extends Application {

    private String facultyName;

    // Constructor to accept faculty name
    public HomePage2(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public void start(Stage primaryStage) {
        // Logo Image setup
        Image logoImage = new Image("/StudentManagement/");
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(150);  // Adjust logo size
        logoImageView.setPreserveRatio(true);

        // Buttons for Add operations
        Button addStudentButton = new Button("Add Student");
        Button addCourseButton = new Button("Add Course");
        Button addSubjectButton = new Button("Add Subject");
        Button backButton = new Button("Back");

        // Set styles for buttons
        setButtonStyle(addStudentButton);
        setButtonStyle(addCourseButton);
        setButtonStyle(addSubjectButton);
        setButtonStyle(backButton);

        // Back Button (Redirects to HomePage and passes the faculty name back)
        backButton.setOnAction(e -> {
            HomePage homePage = new HomePage(facultyName);  // Pass faculty name back to HomePage
            homePage.start(primaryStage);  // Navigate back to HomePage
        });

        // Add Student Button (Redirects to AddStudent Page)
        addStudentButton.setOnAction(e -> {
            AddStudent addStudentPage = new AddStudent();
            addStudentPage.start(primaryStage);
        });

     // Add Course Button (Redirects to AddCourse Page)
        addCourseButton.setOnAction(e -> {
            AddCourse addCoursePage = new AddCourse(facultyName);  // Pass faculty name
            addCoursePage.start(primaryStage);
        });

        // Add Subject Button (Redirects to AddSubject Page)
        addSubjectButton.setOnAction(e -> {
            AddSubject addSubjectPage = new AddSubject(facultyName);  // Pass faculty name
            addSubjectPage.start(primaryStage);
        });


        // Set up GridPane layout for button arrangement
        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);  // Vertical gap between buttons
        gridPane.setHgap(20);  // Horizontal gap between buttons
        gridPane.setAlignment(Pos.CENTER);

        // Adding buttons to the grid
        gridPane.add(addStudentButton, 0, 0);
        gridPane.add(addCourseButton, 1, 0);
        gridPane.add(addSubjectButton, 0, 1);
        gridPane.add(backButton, 1, 1);

        // Setting up the root layout (VBox for the buttons and the logo)
        VBox root = new VBox(20);  // Adjust space between the elements
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FFFFE0;");

        // Adding the logo, and gridPane to the VBox
        root.getChildren().addAll(logoImageView, gridPane);

        // Scene setup
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Home Page 2 - Add Operations");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setButtonStyle(Button button) {
        button.setStyle("-fx-pref-width: 180px; -fx-pref-height: 120px; -fx-font-size: 12px; -fx-font-weight: bold;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
