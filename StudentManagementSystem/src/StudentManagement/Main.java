package StudentManagement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Title label with adjusted font size for smaller window
        Label titleLabel = new Label("Student Management System");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 24px;");  // Reduced font size for smaller window

        // Load logo image
        Image logoImage = new Image(getClass().getResource("/StudentManagement/").toExternalForm());  // Ensure the path is correct
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(80);  // Adjust the height to fit smaller window
        logoImageView.setFitWidth(80);   // Adjust the width to fit smaller window

        // Sub-title label below the logo
        Label subTitleLabel = new Label("Done by ________"); // Enter your Name
        subTitleLabel.setStyle("-fx-font-size: 16px;");  // Slightly smaller font for compact layout

        // Continue button
        Button continueButton = new Button("Continue");
        continueButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();  // Navigate to LoginPage
            loginPage.start(primaryStage);
        });

        // VBox layout to arrange labels and button vertically
        VBox vbox = new VBox(15);  // Adjusted spacing between elements
        vbox.setAlignment(Pos.CENTER);  // Align all elements in the center
        vbox.setStyle("-fx-background-color: #FFFFE0;");  // Light yellow background color
        vbox.getChildren().addAll(titleLabel, logoImageView, subTitleLabel, continueButton);

        // Set up the scene with smaller dimensions
        Scene scene = new Scene(vbox, 450, 350);  // Slightly increased width and height

        // Set up the stage
        primaryStage.setTitle("Student Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Prevent resizing
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
