package StudentManagement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.IntegerStringConverter;
import java.sql.*;

public class EnterMark extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin@123";

    private TableView<StudentMarks> marksTable = new TableView<>();
    private ComboBox<String> courseComboBox = new ComboBox<>();
    private ComboBox<String> branchComboBox = new ComboBox<>();
    private ComboBox<String> yearOfJoiningComboBox = new ComboBox<>();
    private Button submitButton = new Button("Submit");

    @Override
    public void start(Stage primaryStage) {
        // Title label
        Label titleLabel = new Label("Enter Internal Marks out of 20");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 40px;");

        // Populate dropdowns
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
        Button loadButton = new Button("Load Students");
        loadButton.setOnAction(e -> loadStudentData());

        // Set up the table for marks entry
        setUpMarksTable();

        // Submit Button to save marks to database
        submitButton.setOnAction(e -> saveMarksToDatabase());

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Load StudentPerformance.java class (navigate to it)
            new StudentPerformance().start(new Stage());  // Assuming StudentPerformance extends Application
            primaryStage.close();
        });

        // Layout setup
        VBox vbox = new VBox(20, titleLabel, dropdownBox, loadButton, marksTable, submitButton, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #FFFFE0;");

        Scene scene = new Scene(vbox, 1000, 700);
        primaryStage.setTitle("Enter Internal Marks");
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

    // Method to set up the table for student marks entry
    private void setUpMarksTable() {
        TableColumn<StudentMarks, String> registerNumberColumn = new TableColumn<>("Register Number");
        registerNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registerNumber"));

        TableColumn<StudentMarks, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StudentMarks, Integer> test1Column = new TableColumn<>("Test 1 Marks");
        test1Column.setCellValueFactory(new PropertyValueFactory<>("test1Marks"));
        test1Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        test1Column.setOnEditCommit(event -> event.getRowValue().setTest1Marks(event.getNewValue()));

        TableColumn<StudentMarks, Integer> test2Column = new TableColumn<>("Test 2 Marks");
        test2Column.setCellValueFactory(new PropertyValueFactory<>("test2Marks"));
        test2Column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        test2Column.setOnEditCommit(event -> event.getRowValue().setTest2Marks(event.getNewValue()));

        TableColumn<StudentMarks, Integer> totalMarksColumn = new TableColumn<>("Total Marks (out of 40)");
        totalMarksColumn.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));

        marksTable.getColumns().addAll(registerNumberColumn, nameColumn, test1Column, test2Column, totalMarksColumn);
        marksTable.setEditable(true);
    }

    // Method to load student data
    private void loadStudentData() {
        String course = courseComboBox.getValue();
        String branch = branchComboBox.getValue();
        String yearOfJoining = yearOfJoiningComboBox.getValue();

        if (course != null && branch != null && yearOfJoining != null) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT register_number, name FROM students WHERE course = ? AND branch = ? AND year_joined = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, course);
                    statement.setString(2, branch);
                    statement.setString(3, yearOfJoining);
                    ResultSet resultSet = statement.executeQuery();

                    ObservableList<StudentMarks> studentMarksList = FXCollections.observableArrayList();
                    while (resultSet.next()) {
                        studentMarksList.add(new StudentMarks(resultSet.getString("register_number"), resultSet.getString("name")));
                    }
                    marksTable.setItems(studentMarksList);
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading student data.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select Course, Branch, and Year of Joining.");
        }
    }

    // Method to save marks into the database
    private void saveMarksToDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = "INSERT INTO mark (register_number, test1_marks, test2_marks) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                for (StudentMarks student : marksTable.getItems()) {
                    statement.setString(1, student.getRegisterNumber());
                    statement.setInt(2, student.getTest1Marks());
                    statement.setInt(3, student.getTest2Marks());
                    statement.addBatch();
                }
                statement.executeBatch();
                showAlert(Alert.AlertType.INFORMATION, "Marks Saved", "Marks Saved Successfully.");
                loadStudentData();  // Reload student data to reflect saved marks
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error saving marks: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StudentMarks {
        private String registerNumber;
        private String name;
        private int test1Marks;
        private int test2Marks;
        private int totalMarks;

        public StudentMarks(String registerNumber, String name) {
            this.registerNumber = registerNumber;
            this.name = name;
            this.test1Marks = 0;
            this.test2Marks = 0;
            this.totalMarks = 0;
        }

        public String getRegisterNumber() {
            return registerNumber;
        }

        public String getName() {
            return name;
        }

        public int getTest1Marks() {
            return test1Marks;
        }

        public void setTest1Marks(int test1Marks) {
            this.test1Marks = test1Marks;
            calculateTotalMarks();
        }

        public int getTest2Marks() {
            return test2Marks;
        }

        public void setTest2Marks(int test2Marks) {
            this.test2Marks = test2Marks;
            calculateTotalMarks();
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        private void calculateTotalMarks() {
            totalMarks = test1Marks + test2Marks;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
