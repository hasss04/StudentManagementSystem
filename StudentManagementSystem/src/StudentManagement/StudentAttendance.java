package StudentManagement;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentAttendance extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private TableView<StudentAttendanceRecord> table = new TableView<>();
    private ComboBox<String> courseComboBox = new ComboBox<>();
    private ComboBox<String> branchComboBox = new ComboBox<>();
    private ComboBox<String> yearComboBox = new ComboBox<>();
    private DatePicker monthPicker = new DatePicker();

    private ObservableList<StudentAttendanceRecord> students = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        Label titleLabel = new Label("Student Attendance");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        courseComboBox.getItems().addAll("BTech", "B.E");
        courseComboBox.setPromptText("Select Course");
        branchComboBox.setPromptText("Select Branch");
        branchComboBox.setDisable(true);
        yearComboBox.getItems().addAll("2020", "2021", "2022", "2023","2024");
        yearComboBox.setPromptText("Select Year of Joining");
        monthPicker.setPromptText("Select Month");

        courseComboBox.setOnAction(e -> {
            branchComboBox.getItems().clear();
            branchComboBox.setDisable(false);
            if ("BTech".equals(courseComboBox.getValue())) {
                branchComboBox.getItems().addAll("AI & DS", "BioTechnology", "IT", "Agricultural Engineering", "CSBS");
            } else {
                branchComboBox.getItems().addAll("CSE", "ECE", "EEE", "Mechanical Engineering");
            }
        });

        setupTable();

        Button loadStudentsButton = new Button("Load Students");
        loadStudentsButton.setOnAction(e -> loadStudents());

        Button submitButton = new Button("Submit Attendance");
        submitButton.setOnAction(e -> saveAttendance());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToHomePage(primaryStage));

        HBox topLayout = new HBox(10, courseComboBox, branchComboBox, yearComboBox, monthPicker);
        topLayout.setAlignment(Pos.CENTER);

        VBox finalLayout = new VBox(20, titleLabel, topLayout, loadStudentsButton, table, submitButton, backButton);
        finalLayout.setAlignment(Pos.CENTER);
        finalLayout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(finalLayout, 1200, 700);
        primaryStage.setTitle("Student Attendance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTable() {
        TableColumn<StudentAttendanceRecord, String> registerNumberColumn = new TableColumn<>("Register No.");
        registerNumberColumn.setCellValueFactory(cellData -> cellData.getValue().registerNumber);

        TableColumn<StudentAttendanceRecord, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().name);

        List<TableColumn<StudentAttendanceRecord, String>> periodColumns = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            final int periodIndex = i;
            TableColumn<StudentAttendanceRecord, String> periodColumn = new TableColumn<>("Period " + i);
            periodColumn.setCellFactory(ComboBoxTableCell.forTableColumn("Present", "Absent", "OD"));
            periodColumn.setCellValueFactory(cellData -> cellData.getValue().getPeriod(periodIndex));
            periodColumn.setOnEditCommit(event -> {
                event.getRowValue().setPeriod(periodIndex, event.getNewValue());
                event.getRowValue().calculatePercentage();
                table.refresh();
            });
            periodColumns.add(periodColumn);
        }

        TableColumn<StudentAttendanceRecord, String> percentageColumn = new TableColumn<>("Attendance (%)");
        percentageColumn.setCellValueFactory(cellData -> cellData.getValue().percentage);

        table.getColumns().addAll(registerNumberColumn, nameColumn);
        table.getColumns().addAll(periodColumns);
        table.getColumns().add(percentageColumn);
        table.setEditable(true);
    }

    private void loadStudents() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT register_number, name FROM students WHERE course = ? AND branch = ? AND year_joined = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, courseComboBox.getValue());
            stmt.setString(2, branchComboBox.getValue());
            stmt.setString(3, yearComboBox.getValue());

            ResultSet resultSet = stmt.executeQuery();
            students.clear();
            while (resultSet.next()) {
                students.add(new StudentAttendanceRecord(resultSet.getString("register_number"), resultSet.getString("name")));
            }
            table.setItems(students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveAttendance() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String query = "INSERT INTO stuattendance (register_number, course, branch, date, status, attendance_percentage) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (StudentAttendanceRecord record : students) {
                stmt.setString(1, record.registerNumber.get()); 
                stmt.setString(2, courseComboBox.getValue());   
                stmt.setString(3, branchComboBox.getValue());  
                stmt.setDate(4, java.sql.Date.valueOf(monthPicker.getValue().withDayOfMonth(1)));
                stmt.setString(5, "Present");
                stmt.setInt(6, Integer.parseInt(record.percentage.get()));
                stmt.executeUpdate();
            }

            showAlert("Attendance saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

  
    private void goBackToHomePage(Stage primaryStage) {
        HomePage homePage = new HomePage("Faculty Name");
        homePage.start(primaryStage);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class StudentAttendanceRecord {
        private final SimpleStringProperty registerNumber;
        private final SimpleStringProperty name;
        private final SimpleStringProperty[] periods;
        private final SimpleStringProperty percentage;

        public StudentAttendanceRecord(String registerNumber, String name) {
            this.registerNumber = new SimpleStringProperty(registerNumber);
            this.name = new SimpleStringProperty(name);
            this.periods = new SimpleStringProperty[8];
            for (int i = 0; i < 8; i++) {
                periods[i] = new SimpleStringProperty("Absent");
            }
            this.percentage = new SimpleStringProperty("0");
        }

        public SimpleStringProperty getPeriod(int index) { return periods[index - 1]; }

        public void setPeriod(int index, String value) {
            periods[index - 1].set(value);
            calculatePercentage();
        }

        public void calculatePercentage() {
            int count = 0;
            for (SimpleStringProperty period : periods) {
                if (period.get().equals("Present") || period.get().equals("OD")) count++;
            }
            int percentageValue = (int) ((count / 8.0) * 100);
            percentage.set(String.valueOf(percentageValue));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
