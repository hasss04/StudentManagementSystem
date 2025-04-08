package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class AddStudent extends Application {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    @Override
    public void start(Stage primaryStage) {
        // Title
        Label titleLabel = new Label("Add Student");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        // Creating the form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Enter student name");

        TextField registerNumberField = new TextField();
        registerNumberField.setPromptText("Enter register number (12 digits)");

        ComboBox<String> courseComboBox = new ComboBox<>();
        courseComboBox.getItems().addAll("BTech", "B.E");
        courseComboBox.setPromptText("Select Course");

        ComboBox<String> branchComboBox = new ComboBox<>();
        branchComboBox.setPromptText("Select Branch");
        branchComboBox.setDisable(true); // Disable initially

        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Others");

        ComboBox<String> bloodGroupComboBox = new ComboBox<>();
        bloodGroupComboBox.getItems().addAll("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");

        ComboBox<String> scholarTypeComboBox = new ComboBox<>();
        scholarTypeComboBox.getItems().addAll("Day Scholar", "Hosteller");

        // Add ComboBoxes for Year of Joining and Year of Graduation
        ComboBox<String> joiningYearComboBox = new ComboBox<>();
        joiningYearComboBox.getItems().addAll("2020", "2021", "2022", "2023", "2024"); // You can adjust this list based on your needs
        joiningYearComboBox.setPromptText("Select Year of Joining");

        ComboBox<String> graduationYearComboBox = new ComboBox<>();
        graduationYearComboBox.getItems().addAll("2024", "2025", "2026", "2027", "2028"); // You can adjust this list based on your needs
        graduationYearComboBox.setPromptText("Select Year of Graduation");

        TextField fatherNameField = new TextField();
        fatherNameField.setPromptText("Father's Name");

        TextField motherNameField = new TextField();
        motherNameField.setPromptText("Mother's Name");

        TextArea addressField = new TextArea();
        addressField.setPromptText("Enter address");

        TextField parentPhoneField = new TextField();
        parentPhoneField.setPromptText("Parent's Phone Number");

        TextField studentPhoneField = new TextField();
        studentPhoneField.setPromptText("Student's Phone Number");

        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select Date of Birth");

        courseComboBox.setOnAction(e -> {
            String selectedCourse = courseComboBox.getValue();
            branchComboBox.getItems().clear();
            if ("BTech".equals(selectedCourse)) {
                branchComboBox.getItems().addAll("AI & DS", "BioTechnology", "IT", "Agricultural Engineering", "Computer Science and Business Systems");
            } else if ("B.E".equals(selectedCourse)) {
                branchComboBox.getItems().addAll("CSE", "ECE", "EEE", "Mechanical Engineering");
            }
            branchComboBox.setDisable(false);
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || registerNumberField.getText().length() != 12 ||
                courseComboBox.getValue() == null || branchComboBox.getValue() == null || genderComboBox.getValue() == null ||
                bloodGroupComboBox.getValue() == null || scholarTypeComboBox.getValue() == null ||
                fatherNameField.getText().isEmpty() || motherNameField.getText().isEmpty() ||
                addressField.getText().isEmpty() || parentPhoneField.getText().isEmpty() ||
                studentPhoneField.getText().isEmpty() || joiningYearComboBox.getValue() == null || graduationYearComboBox.getValue() == null) {

                showAlert(Alert.AlertType.ERROR, "Form Incomplete", "Please fill all the fields properly.");
                return;
            }

            // Check if phone number is 10 digits
            if (parentPhoneField.getText().length() != 10 || studentPhoneField.getText().length() != 10) {
                showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Enter 10 Digits Phone Number");
                return;
            }

            // Check age restriction
            LocalDate dob = dobPicker.getValue();
            if (dob != null) {
                int age = Period.between(dob, LocalDate.now()).getYears();
                if (age < 17 || age > 25) {
                    showAlert(Alert.AlertType.ERROR, "Age Restriction", "Student age must be between 17 and 25.");
                    return;
                }
            }

            // Check if register number or phone number already exists
            if (checkIfDetailsExist(registerNumberField.getText(), parentPhoneField.getText(), studentPhoneField.getText())) {
                showAlert(Alert.AlertType.ERROR, "Details Already Exists", "Register Number or Phone Number already exists.");
                return;
            }

            saveStudentDetails(nameField.getText(), registerNumberField.getText(), courseComboBox.getValue(),
                    branchComboBox.getValue(), dobPicker.getValue(), genderComboBox.getValue(), bloodGroupComboBox.getValue(),
                    scholarTypeComboBox.getValue(), fatherNameField.getText(), motherNameField.getText(), addressField.getText(),
                    parentPhoneField.getText(), studentPhoneField.getText(), joiningYearComboBox.getValue(), graduationYearComboBox.getValue());
        });

        // Logo and text at the top
        Image logoImage = new Image(getClass().getResource("/StudentManagement/logo1.png").toExternalForm());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(100);
        logoImageView.setFitWidth(100);

        Label systemLabel = new Label("Student Management System");
        systemLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox headerBox = new VBox(10, logoImageView, systemLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        // Back Button
        Button backButton = new Button("← Back");
        backButton.setStyle("-fx-font-size: 18px; -fx-background-color: #ffcc00;");
        backButton.setOnAction(e -> {
            HomePage homePage = new HomePage("Faculty Name");
            Stage homeStage = new Stage();
            homePage.start(homeStage);
            primaryStage.close();
        });

        // Form Grid
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setMaxWidth(700);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(titleLabel, 0, 0, 2, 1);

        gridPane.add(new Label("Student Name:"), 0, 1);
        gridPane.add(nameField, 1, 1);

        gridPane.add(new Label("Register Number:"), 0, 2);
        gridPane.add(registerNumberField, 1, 2);

        gridPane.add(new Label("Course:"), 0, 3);
        gridPane.add(courseComboBox, 1, 3);

        gridPane.add(new Label("Branch:"), 0, 4);
        gridPane.add(branchComboBox, 1, 4);

        gridPane.add(new Label("Date of Birth:"), 0, 5);
        gridPane.add(dobPicker, 1, 5);

        gridPane.add(new Label("Gender:"), 0, 6);
        gridPane.add(genderComboBox, 1, 6);

        gridPane.add(new Label("Blood Group:"), 0, 7);
        gridPane.add(bloodGroupComboBox, 1, 7);

        gridPane.add(new Label("Scholar Type:"), 0, 8);
        gridPane.add(scholarTypeComboBox, 1, 8);

        gridPane.add(new Label("Father's Name:"), 0, 9);
        gridPane.add(fatherNameField, 1, 9);

        gridPane.add(new Label("Mother's Name:"), 0, 10);
        gridPane.add(motherNameField, 1, 10);

        gridPane.add(new Label("Address:"), 0, 11);
        gridPane.add(addressField, 1, 11);

        gridPane.add(new Label("Parent's Phone:"), 0, 12);
        gridPane.add(parentPhoneField, 1, 12);

        gridPane.add(new Label("Student's Phone:"), 0, 13);
        gridPane.add(studentPhoneField, 1, 13);

        gridPane.add(new Label("Year of Joining:"), 0, 14);
        gridPane.add(joiningYearComboBox, 1, 14);

        gridPane.add(new Label("Year of Graduation:"), 0, 15);
        gridPane.add(graduationYearComboBox, 1, 15);

        gridPane.add(submitButton, 1, 16);
        gridPane.add(backButton, 1, 17);

        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(gridPane);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Student");
        primaryStage.show();
    }

    private boolean checkIfDetailsExist(String registerNumber, String parentPhone, String studentPhone) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM students WHERE register_number = ? OR parent_phone = ? OR student_phone = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, registerNumber);
            stmt.setString(2, parentPhone);
            stmt.setString(3, studentPhone);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveStudentDetails(String name, String registerNumber, String course, String branch, LocalDate dob, String gender,
                                     String bloodGroup, String scholarType, String fatherName, String motherName, String address,
                                     String parentPhone, String studentPhone, String joiningYear, String graduationYear) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO students (name, register_number, course, branch, dob, gender, blood_group, " +
                    "day_scholar, father_name, mother_name, address, parent_phone, student_phone, year_joined, year_graduation) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, registerNumber);
            stmt.setString(3, course);
            stmt.setString(4, branch);
            stmt.setDate(5, Date.valueOf(dob));
            stmt.setString(6, gender);
            stmt.setString(7, bloodGroup);
            stmt.setString(8, scholarType);
            stmt.setString(9, fatherName);
            stmt.setString(10, motherName);
            stmt.setString(11, address);
            stmt.setString(12, parentPhone);
            stmt.setString(13, studentPhone);
            stmt.setString(14, joiningYear);
            stmt.setString(15, graduationYear);
            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Student added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save student details.");
        }
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
