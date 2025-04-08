package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Studentinfo extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private TableView<StudentDetails> studentTable = new TableView<>();
    private ComboBox<String> courseComboBox = new ComboBox<>();
    private ComboBox<String> branchComboBox = new ComboBox<>();
    private ComboBox<String> yearOfJoiningComboBox = new ComboBox<>();
    private Button loadButton = new Button("Load Students");

    @Override
    public void start(Stage primaryStage) {
        // Title label
        Label titleLabel = new Label("Student Information and Marks");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 40px;");

        // Populate dropdowns for Course, Branch, and Year
        populateDropdowns();

        // Labels for dropdowns
        Label courseLabel = new Label("Course:");
        Label branchLabel = new Label("Branch:");
        Label yearLabel = new Label("Year of Joining:");

        // Setting predefined values for yearOfJoiningComboBox
        yearOfJoiningComboBox.getItems().addAll("2020", "2021", "2022", "2023", "2024");

        // Course ComboBox listener to update Branch ComboBox
        courseComboBox.setOnAction(e -> updateBranchOptions());

        // Layout for dropdowns
        HBox dropdownBox = new HBox(15, 
                new VBox(courseLabel, courseComboBox), 
                new VBox(branchLabel, branchComboBox), 
                new VBox(yearLabel, yearOfJoiningComboBox));
        dropdownBox.setAlignment(Pos.CENTER);

        // Button to load student data
        loadButton.setOnAction(e -> loadStudentData());

        // Set up the table for student information display
        setUpStudentTable();

        // Layout setup
        VBox vbox = new VBox(20, titleLabel, dropdownBox, loadButton, studentTable);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #FFFFE0;");

        Scene scene = new Scene(vbox, 1000, 700);
        primaryStage.setTitle("Student Information");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to populate dropdowns with data from the database
    private void populateDropdowns() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT DISTINCT course FROM students");
            while (rs.next()) {
                courseComboBox.getItems().add(rs.getString("course"));
            }

            rs = statement.executeQuery("SELECT DISTINCT branch FROM students");
            while (rs.next()) {
                branchComboBox.getItems().add(rs.getString("branch"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error fetching dropdown data.");
        }
    }

    // Method to update Branch dropdown based on selected Course
    private void updateBranchOptions() {
        String selectedCourse = courseComboBox.getValue();
        branchComboBox.getItems().clear();
        if ("BTech".equals(selectedCourse)) {
            branchComboBox.getItems().addAll("AI & DS", "BioTechnology", "IT", "Agricultural Engineering", "Computer Science and Business Systems");
        } else if ("B.E".equals(selectedCourse)) {
            branchComboBox.getItems().addAll("CSE", "ECE", "EEE", "Mechanical Engineering");
        }
    }

    // Method to set up the table for displaying student details and marks
    private void setUpStudentTable() {
        TableColumn<StudentDetails, String> registerNumberColumn = new TableColumn<>("Register Number");
        registerNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registerNumber"));

        TableColumn<StudentDetails, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StudentDetails, String> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));

        TableColumn<StudentDetails, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<StudentDetails, String> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));

        TableColumn<StudentDetails, String> branchColumn = new TableColumn<>("Branch");
        branchColumn.setCellValueFactory(new PropertyValueFactory<>("branch"));

        TableColumn<StudentDetails, Integer> test1Column = new TableColumn<>("Test 1 Marks");
        test1Column.setCellValueFactory(new PropertyValueFactory<>("test1Marks"));

        TableColumn<StudentDetails, Integer> test2Column = new TableColumn<>("Test 2 Marks");
        test2Column.setCellValueFactory(new PropertyValueFactory<>("test2Marks"));

        studentTable.getColumns().addAll(registerNumberColumn, nameColumn, dobColumn, genderColumn, courseColumn, branchColumn, test1Column, test2Column);
    }

    // Method to load student data from the database
    private void loadStudentData() {
        String course = courseComboBox.getValue();
        String branch = branchComboBox.getValue();
        String yearOfJoining = yearOfJoiningComboBox.getValue();

        if (course != null && branch != null && yearOfJoining != null) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT s.register_number, s.name, s.dob, s.gender, s.course, s.branch, m.test1_marks, m.test2_marks " +
                        "FROM students s LEFT JOIN mark m ON s.register_number = m.register_number " +
                        "WHERE s.course = ? AND s.branch = ? AND s.year_joined = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, course);
                    statement.setString(2, branch);
                    statement.setString(3, yearOfJoining);
                    ResultSet resultSet = statement.executeQuery();

                    ObservableList<StudentDetails> studentDetailsList = FXCollections.observableArrayList();
                    while (resultSet.next()) {
                        studentDetailsList.add(new StudentDetails(
                                resultSet.getString("register_number"),
                                resultSet.getString("name"),
                                resultSet.getString("dob"),
                                resultSet.getString("gender"),
                                resultSet.getString("course"),
                                resultSet.getString("branch"),
                                resultSet.getInt("test1_marks"),
                                resultSet.getInt("test2_marks")
                        ));
                    }
                    studentTable.setItems(studentDetailsList);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading student data.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select Course, Branch, and Year of Joining.");
        }
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StudentDetails {
        private String registerNumber;
        private String name;
        private String dob;
        private String gender;
        private String course;
        private String branch;
        private int test1Marks;
        private int test2Marks;

        public StudentDetails(String registerNumber, String name, String dob, String gender, String course, String branch, int test1Marks, int test2Marks) {
            this.registerNumber = registerNumber;
            this.name = name;
            this.dob = dob;
            this.gender = gender;
            this.course = course;
            this.branch = branch;
            this.test1Marks = test1Marks;
            this.test2Marks = test2Marks;
        }

        public String getRegisterNumber() {
            return registerNumber;
        }

        public String getName() {
            return name;
        }

        public String getDob() {
            return dob;
        }

        public String getGender() {
            return gender;
        }

        public String getCourse() {
            return course;
        }

        public String getBranch() {
            return branch;
        }

        public int getTest1Marks() {
            return test1Marks;
        }

        public int getTest2Marks() {
            return test2Marks;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
