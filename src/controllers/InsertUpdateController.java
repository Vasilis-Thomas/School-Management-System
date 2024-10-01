package controllers;

import com.jcraft.jsch.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javafx.stage.Stage;

public class InsertUpdateController {

    private static List<String> genderList = null, classroomList = null,
            genderTeacherList = null, specialtyTeacherList = null,
            classesList = null, orientationsList = null,
            orientationClassesList = null, courseCodeList = null;

    private static List<Integer> semesterList = null;
    private static Session session = null;
    private static Connection dbConnection = null;
    @FXML
    private Tab sshTab, dbConnectTab;
    @FXML
    private Label label1, label2, label3, label4, label5, label6, label7, titleLabel;
    @FXML
    private TextField field1, field2, field3, field5, field6, field7;
    @FXML
    private DatePicker field4;
    @FXML
    private ComboBox<String> combobox1, combobox2;

    @FXML
    private TextArea textArea1;
    @FXML
    private Button btn;


    public static Connection getDbConnection() {
        return dbConnection;
    }

    //-------- STUDENTS --------\\

    public void initializeStudents() throws IOException {
        genderList = TableViewController.genderStudentList;
        classroomList = TableViewController.classroomsList;

//        genderStudentList.remove(0);
//        classroomList.remove(0);

//        System.out.println("controllers.InsertUpdateController.initializeStudents classroomList[] :");
//        for (int i = 0; i < classroomList.size(); i++) {
//            System.out.println(i + " --> " + classroomList.get(i));
//        }
//
//        System.out.println("controllers.InsertUpdateController.initializeStudents genderStudentList[] :");
//        for (int i = 0; i < genderList.size(); i++) {
//            System.out.println(i + " --> " + genderList.get(i));
//        }

        combobox1.getItems().addAll(genderList);
        combobox2.getItems().addAll(classroomList);

        // Set label texts
        btn.setText("ADD STUDENT");
        titleLabel.setText("Add Student");
        label1.setText("SID");
        label2.setText("First Name");
        label3.setText("Last Name");
        label4.setText("Date of Birth");
//        label.setText("Student Gender");
        label5.setText("Email");
        label6.setText("Phone");
        label7.setText("Address");
//        label.setText("Classroom");
        combobox1.setPromptText("All Genders");
        combobox2.setPromptText("All Classrooms");

        // Modify visibility and edit-ability
        field1.setEditable(false);
        field2.setPromptText("*");
        field3.setPromptText("*");
        field4.setPromptText("dd/mm/yyyy");

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                insertStudentButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void insertStudentButton() throws ParseException {

//        textField1AddForm.getText();
//        textField2AddForm.getText();
//        textField3AddForm.getText();
//        textField5AddForm.getText();
//        textField4AddForm.getText();
//        textField7AddForm.getText();
//        textField6AddForm.getText();
//        textField8AddForm.getText();
//        textField9AddForm.getText();
//        textField10AddForm.getText();

        checkFormValidationForStudent();

        // Getting given by user values
//        Integer sid = Integer.valueOf(textField1AddForm.getText());
        String firstName_var = field2.getText();
        String lastName_var = field3.getText();
        String gender_var = combobox1.getValue();
        System.out.println("gender_var: " + gender_var);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        java.util.Date parsedDate_birth_var = sdf.parse(field4.getEditor().getText());
        Date birth_var = new java.sql.Date(parsedDate_birth_var.getTime());
        System.out.println(birth_var);

        String email_var = field5.getText();
        String phone_var = field6.getText();
        String address_var = field7.getText();
        String classroom_name = (String) combobox2.getValue();

        // insert student
        try {
            String sqlINSERT = "select * from insertStudent(?,?,?,?,?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, firstName_var);
            preparedStatement.setString(2, lastName_var);
            preparedStatement.setDate(3, birth_var);
            preparedStatement.setString(4, gender_var);
            preparedStatement.setString(5, email_var);
            preparedStatement.setString(6, classroom_name);
            preparedStatement.setString(7, phone_var);
            preparedStatement.setString(8, address_var);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
//        textField1AddForm.setText("");
            field2.setText("");
            field3.setText("");
            field4.setValue(null);
            field5.setText("");
            field6.setText("");
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("All Genders");
            combobox2.setPromptText("All Classrooms");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Successfully Message");
            infoAlert.setHeaderText("Congratulations,");
            infoAlert.setContentText("Student named " + firstName_var + " " + lastName_var + " added in our school!");
            infoAlert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeStudentsUpdate(int id) throws SQLException {

        genderList = TableViewController.genderStudentList;
        classroomList = TableViewController.classroomsList;

        combobox1.getItems().addAll(genderList);
        combobox2.getItems().addAll(classroomList);

        // Set label texts
        btn.setText("UPDATE STUDENT");
        titleLabel.setText("Edit Student");
        label1.setText("SID");
        label2.setText("First Name");
        label3.setText("Last Name");
        label4.setText("Date of Birth");
        label5.setText("Email");
        label6.setText("Phone");
        label7.setText("Address");
        combobox1.setPromptText("All Genders");
        combobox2.setPromptText("All Classrooms");

        // Modify visibility and edit-ability
        field1.setVisible(true);
        field1.setEditable(false);
        field2.setPromptText("*");
        field3.setPromptText("*");
        field4.setPromptText("dd/mm/yyyy");

        // Setting fields values according to id
        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getStudent(?)");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            field1.setText(String.valueOf(resultSet.getInt(1)));
            field2.setText(String.valueOf(resultSet.getString(2)));
            field3.setText(String.valueOf(resultSet.getString(3)));
            field4.setValue(LocalDate.parse(String.valueOf(resultSet.getDate(4))));
            combobox1.setValue(String.valueOf(resultSet.getString(5)));
            combobox2.setValue(String.valueOf(resultSet.getString(6)));
            field5.setText(String.valueOf(resultSet.getString(7)));
            field6.setText(String.valueOf(resultSet.getString(8)));
            field7.setText(String.valueOf(resultSet.getString(9)));
        }

        preparedStatement.close();

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                updateStudentButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateStudentButton() throws ParseException {

        checkFormValidationForStudent();

        // Getting given by user values
        Integer sid = Integer.valueOf(field1.getText());
        String firstName_var = field2.getText();
        String lastName_var = field3.getText();
        String gender_var = combobox1.getValue();
        System.out.println("gender_var: " + gender_var);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        java.util.Date parsedDate_birth_var = sdf.parse(field4.getEditor().getText());
        Date birth_var = new java.sql.Date(parsedDate_birth_var.getTime());
        System.out.println(birth_var);

        String email_var = field5.getText();
        String phone_var = field6.getText();
        String address_var = field7.getText();
        String classroom_name = (String) combobox2.getValue();

        // Update student
        try {

            String sqlINSERT = "select * from updateStudent(?,?,?,?,?,?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, firstName_var);
            preparedStatement.setString(2, lastName_var);
            preparedStatement.setDate(3, birth_var);
            preparedStatement.setString(4, gender_var);
            preparedStatement.setString(6, classroom_name);
            preparedStatement.setString(5, email_var);
            preparedStatement.setString(7, phone_var);
            preparedStatement.setString(8, address_var);
            preparedStatement.setInt(9, sid);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field1.setText("");
            field2.setText("");
            field3.setText("");
            field4.setValue(null);
            field5.setText("");
            field6.setText("");
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("All Genders");
            combobox2.setPromptText("All Classrooms");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Successfully Message");
            infoAlert.setHeaderText("Well,");
            infoAlert.setContentText("Student named " + firstName_var + " " + lastName_var + " has been updated!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//-------- TEACHERS --------\\

    public void initializeTeachers() throws IOException {

        genderTeacherList = TableViewController.genderTeacherList;
        specialtyTeacherList = TableViewController.specialtyTeacherList;

        combobox1.getItems().addAll(genderTeacherList);
        combobox2.getItems().addAll(specialtyTeacherList);

        btn.setText("ADD TEACHER");
        titleLabel.setText("Add Teacher");
        label1.setText("TID");
        label2.setText("First Name");
        label3.setText("Last Name");
        label5.setText("Email");
        label6.setText("Phone");
        label7.setText("Address");


        combobox1.setPromptText("All Genders");
        combobox2.setPromptText("All Specialties");

        field1.setEditable(false);
        label4.setVisible(false);
        field4.setVisible(false);
        field2.setPromptText("*");
        field3.setPromptText("*");

        btn.setOnAction(event -> {
            try {
                insertTeacherButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void insertTeacherButton() throws ParseException {

        checkFormValidationForTeacher();

        // Getting given by user values
        String firstName_var = field2.getText();
        String lastName_var = field3.getText();
        String gender_var = combobox1.getValue();
        System.out.println("gender_var: " + gender_var);

//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//        java.util.Date parsedDate_birth_var = sdf.parse(datepicker1.getEditor().getText());
//        Date birth_var = new java.sql.Date(parsedDate_birth_var.getTime());
//        System.out.println(birth_var);

        String email_var = field5.getText();
        String phone_var = field6.getText();
        String address_var = field7.getText();
        String specialty_var = combobox2.getSelectionModel().getSelectedItem();

        // insert teacher
        try {
            String sqlINSERT = "select * from insertTeacher(?,?,?,?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, firstName_var);
            preparedStatement.setString(2, lastName_var);
            preparedStatement.setString(3, gender_var);
            preparedStatement.setString(4, specialty_var);
            preparedStatement.setString(5, email_var);
            preparedStatement.setString(6, phone_var);
            preparedStatement.setString(7, address_var);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field2.setText("");
            field3.setText("");
            field5.setText("");
            field6.setText("");
//            datepicker1.setValue(null);
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("All Genders");
            combobox2.setPromptText("All Specialties");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Successfully Message");
            infoAlert.setHeaderText("Congratulations,");
            infoAlert.setContentText("Teacher named " + firstName_var + " " + lastName_var + " added in our school!");
            infoAlert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeTeachersUpdate(int id) throws SQLException {
        genderList = TableViewController.genderTeacherList;
        specialtyTeacherList = TableViewController.specialtyTeacherList;

        // Set items in comboboxes
        combobox1.getItems().addAll(genderList);
        combobox2.getItems().addAll(specialtyTeacherList);

        // Set label texts
        btn.setText("UPDATE TEACHER");
        titleLabel.setText("Edit Teacher");
        label1.setText("TID");
        label2.setText("First Name");
        label3.setText("Last Name");
//        label.setText("Teacher Gender");
//        field5.setText("Date of Birth");
        label5.setText("Email");
        label6.setText("Phone");
        label7.setText("Address");
//        label.setText("Specialty");
        combobox1.setPromptText("All Genders");
        combobox2.setPromptText("All Specialties");

        // Modify visibility and edit-ability
        label4.setVisible(false);
        field1.setEditable(false);
        field2.setPromptText("*");
        field3.setPromptText("*");
        field4.setVisible(false);
//        datepicker1.setPromptText("dd/mm/yyyy");

        // Setting fields values according to id
        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getTeacher(?)");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            field1.setText(String.valueOf(resultSet.getInt(1)));
            field2.setText(String.valueOf(resultSet.getString(2)));
            field3.setText(String.valueOf(resultSet.getString(3)));
//            datepicker1.setValue(LocalDate.parse(String.valueOf(resultSet.getDate(4))));
            combobox1.setValue(String.valueOf(resultSet.getString(4)));
            combobox2.setValue(String.valueOf(resultSet.getString(5)));
            field5.setText(String.valueOf(resultSet.getString(6)));
            field6.setText(String.valueOf(resultSet.getString(7)));
            field7.setText(String.valueOf(resultSet.getString(8)));
        }

        preparedStatement.close();
        resultSet.close();

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                updateTeacherButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateTeacherButton() throws ParseException {

        checkFormValidationForTeacher();

        // Getting given by user values
        Integer tid = Integer.valueOf(field1.getText());
        String firstName_var = field2.getText();
        String lastName_var = field3.getText();
        String gender_var = combobox1.getValue();
        System.out.println("gender_var: " + gender_var);

        String email_var = field5.getText();
        String phone_var = field6.getText();
        String address_var = field7.getText();
        String specialty_var = combobox2.getSelectionModel().getSelectedItem();

        // Update student
        try {

            String sqlINSERT = "select * from updateTeacher(?,?,?,?,?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, firstName_var);
            preparedStatement.setString(2, lastName_var);
            preparedStatement.setString(3, gender_var);
            preparedStatement.setString(4, specialty_var);
            preparedStatement.setString(5, email_var);
            preparedStatement.setString(6, phone_var);
            preparedStatement.setString(7, address_var);
            preparedStatement.setInt(8, tid);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field1.setText("");
            field2.setText("");
            field3.setText("");
            field5.setText("");
            field6.setText("");
            field7.setText("");
//            datepicker1.setValue(null);
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("All Genders");
            combobox2.setPromptText("All Specialties");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Successfully Message");
            infoAlert.setHeaderText("Well,");
            infoAlert.setContentText("Teacher named " + firstName_var + " " + lastName_var + " has been updated!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-------- COURSES --------\\
    public void initializeCourses() throws IOException {
        classesList = TableViewController.classesList;
        orientationsList = TableViewController.orientationsList;

        combobox1.getItems().addAll(classesList);
        combobox2.getItems().addAll(orientationsList);

        // Set label texts
        btn.setText("ADD COURSE");
        titleLabel.setText("Add Course");
        label1.setText("CID");
        label2.setText("Title");
        label3.setText("Code");
        label4.setVisible(false);
        label5.setText("Hours Per Week");
        label6.setText("Description");
        label7.setVisible(false);
        textArea1.setVisible(true);
        field6.setVisible(false);
        field7.setVisible(false);
        field4.setVisible(false);

        combobox1.setPromptText("Class");
        combobox2.setPromptText("Orientation");

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                insertCourseButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void insertCourseButton() throws ParseException {

        checkFormValidationForCourse();

        String title = field2.getText();
        String code = field3.getText();
        Integer hours_per_week = Integer.valueOf(field5.getText());
        String description = textArea1.getText();

        String class_grade = (String) combobox1.getValue();
        String orientation = (String) combobox2.getValue();

        // Insert Course
        try {

            String sqlINSERT = "select * from insertCourse(?,?,?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, code);
            preparedStatement.setInt(3, hours_per_week);
            preparedStatement.setString(4, class_grade);
            preparedStatement.setString(5, orientation);
            preparedStatement.setString(6, description);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field1.setText("");
            field2.setText("");
            field3.setText("");
            field5.setText("");
//            field6.setText("");
//            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("Class");
            combobox2.setPromptText("Orientation");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Success Message");
            infoAlert.setHeaderText("Add Complete,");
            infoAlert.setContentText("Course " + title + " has been added!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeCoursesUpdate(int id) throws SQLException {
        classesList = TableViewController.classesList;
        orientationsList = TableViewController.orientationsList;

        combobox1.getItems().addAll(classesList);
        combobox2.getItems().addAll(orientationsList);

        // Set label texts
        btn.setText("UPDATE COURSE");
        titleLabel.setText("Edit Course");
        label1.setText("CID");
        label2.setText("Title");
        label3.setText("Code");
        label4.setVisible(false);
        label5.setText("Hours Per Week");
        label6.setText("Description");
        label7.setVisible(false);
        textArea1.setVisible(true);
        field6.setVisible(false);
        field7.setVisible(false);
        field4.setVisible(false);

        combobox1.setPromptText("Class");
        combobox2.setPromptText("Orientation");

        // Setting fields values according to id
        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getCourseDetails(?)");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            field1.setText(String.valueOf(resultSet.getInt(1)));
            field2.setText(String.valueOf(resultSet.getString(2)));
            field3.setText(String.valueOf(resultSet.getString(3)));
            field5.setText(String.valueOf(resultSet.getInt(4)));
            textArea1.setText(String.valueOf(resultSet.getString(7)));
            combobox1.setValue(String.valueOf(resultSet.getString(5)));
            combobox2.setValue(String.valueOf(resultSet.getString(6)));
        }

        preparedStatement.close();

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                updateCourseButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateCourseButton() throws ParseException {

        checkFormValidationForCourse();

        // Getting given by user values
        Integer cid = Integer.valueOf(field1.getText());
        String title = field2.getText();
        String code = field3.getText();
        Integer hours_per_week = Integer.valueOf(field5.getText());
        String description = textArea1.getText();

        String class_grade = (String) combobox1.getValue();
        String orientation = (String) combobox2.getValue();

        // Update student
        try {

            String sqlINSERT = "select * from updateCourse(?,?,?,?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setInt(1, cid);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, code);
            preparedStatement.setInt(4, hours_per_week);
            preparedStatement.setString(5, class_grade);
            preparedStatement.setString(6, orientation);
            preparedStatement.setString(7, description);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field1.setText("");
            field2.setText("");
            field3.setText("");
            field5.setText("");
            field6.setText("");
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();

            combobox1.setPromptText("Class");
            combobox2.setPromptText("Orientation");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Success Message");
            infoAlert.setHeaderText("Update Complete,");
            infoAlert.setContentText("Course " + title + " has been updated!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //-------- GRADES --------\\

    public void initializeGrades() throws IOException {
        courseCodeList = TableViewController.courseCodeList;
        semesterList = TableViewController.semesterList;

        combobox1.getItems().addAll(courseCodeList);
        combobox2.getItems().addAll(semesterList.stream()
                .map(Object::toString)
                .toList());

        // Set label texts
        btn.setText("ADD GRADE");
        titleLabel.setText("Add Grade");
        label6.setText("Student id");
        label7.setText("Grade");
        label5.setVisible(false);
        label4.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        textArea1.setVisible(false);
        field4.setVisible(false);
        field2.setVisible(false);
        field3.setVisible(false);
        field5.setVisible(false);
        field1.setVisible(false);
        label1.setVisible(false);
        combobox1.setPromptText("Course");
        combobox2.setPromptText("Semester");

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                insertGradeButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void insertGradeButton() throws ParseException {

        checkFormValidationForGrade();

        int student_id = Integer.parseInt(field6.getText());
        double grade = Double.parseDouble(field7.getText());
        if (grade <0 || grade>20){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Warning");
            alert.setContentText("Please enter a valid double value.");
            alert.showAndWait();
            field7.clear();
            return;
        }


        String course_code = (String) combobox1.getValue();
        int semester;
        try {
            // Try to parse the string as an integer
            semester = Integer.parseInt(combobox2.getSelectionModel().getSelectedItem());
        } catch (NumberFormatException e) {
            // If parsing fails, return a default value or handle it in a specific way
            semester = 0;
        }


        // Insert Grade
        try {

            String sqlINSERT = "select * from insertGrade(?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setInt(1, student_id);
            preparedStatement.setString(2, course_code);
            preparedStatement.setInt(3, semester);
            preparedStatement.setDouble(4, grade);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field6.setText("");
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("Class");
            combobox2.setPromptText("Orientation");

            // Successful message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Success Message");
            infoAlert.setHeaderText("Add Complete,");
            infoAlert.setContentText("Grade has been added!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeGradesUpdate(int sid, String cid, int sem) throws SQLException {
        courseCodeList = TableViewController.courseCodeList;
        semesterList = TableViewController.semesterList;

        combobox1.getItems().addAll(courseCodeList);
        combobox2.getItems().addAll(semesterList.stream()
                .map(Object::toString)
                .toList());

        // Set label texts
        btn.setText("UPDATE GRADE");
        titleLabel.setText("Edit Grade");
        label6.setText("Student id");
        label7.setText("Grade");
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        label4.setVisible(false);
        label5.setVisible(false);
        textArea1.setVisible(false);
        field1.setVisible(false);
        field2.setVisible(false);
        field3.setVisible(false);
        field4.setVisible(false);
        field5.setVisible(false);

        combobox1.setPromptText("Course");
        combobox2.setPromptText("Semester");

        // Setting fields values according to id
        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getGrade(?,?,?)");
        preparedStatement.setInt(1, sid);
        preparedStatement.setString(2, cid);
        preparedStatement.setInt(3, sem);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            field6.setText(String.valueOf(resultSet.getInt(1)));
            field7.setText(String.valueOf(resultSet.getDouble(4)));
            combobox1.setValue(String.valueOf(resultSet.getString(2)));
            combobox2.setValue(String.valueOf(resultSet.getInt(3)));
        }

        preparedStatement.close();

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                updateGradeButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateGradeButton() throws ParseException {

        checkFormValidationForGrade();

        // Getting given by user values
        int sid = Integer.parseInt(field6.getText());
        double grade = Double.parseDouble(field7.getText());

        String course_code = (String) combobox1.getValue();
        int semester;
        try {
            // Try to parse the string as an integer
            semester = Integer.parseInt(combobox2.getSelectionModel().getSelectedItem());
        } catch (NumberFormatException e) {
            // If parsing fails, return a default value or handle it in a specific way
            semester = 0;
        }

        // Update Grade
        try {

            String sqlINSERT = "select * from updateGrade(?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setInt(1, sid);
            preparedStatement.setString(2, course_code);
            preparedStatement.setInt(3, semester);
            preparedStatement.setDouble(4, grade);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field6.setText("");
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();

            combobox1.setPromptText("Course");
            combobox2.setPromptText("Semester");

            // Successful message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Success Message");
            infoAlert.setHeaderText("Update Complete,");
            infoAlert.setContentText("Grade has been updated!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-------- CLASSROOMS --------\\
    public void initializeClassrooms() throws IOException {
        classesList = TableViewController.classesList;
        orientationClassesList = TableViewController.orientationClassesList;

        System.out.println("controllers.InsertUpdateController.initializeClassrooms orientationClassesList[] :");
        for (int i = 0; i < orientationClassesList.size(); i++) {
            System.out.println(i + " --> " + orientationClassesList.get(i));
        }

        System.out.println("controllers.InsertUpdateController.initializeClassrooms classesList[] :");
        for (int i = 0; i < classesList.size(); i++) {
            System.out.println(i + " --> " + classesList.get(i));
        }

        combobox1.getItems().addAll(classesList);
        combobox2.getItems().addAll(orientationClassesList);

        // Set label texts
        btn.setText("ADD CLASSROOM");
        titleLabel.setText("Add Classrooom");
        label6.setText("Classroom ID");
        label7.setText("Classroom Name");
        combobox1.setPromptText("All Classes");
//        combobox2.setPromptText("All Classrooms");

        // Modify visibility and edit-ability
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        label4.setVisible(false);
        label5.setVisible(false);
        label6.setVisible(true);
        label7.setVisible(true);

        field1.setVisible(false);
        field2.setVisible(false);
        field3.setVisible(false);
        field4.setVisible(false);
        field5.setVisible(false);
        field6.setVisible(true);
        field6.setDisable(true);
        field7.setVisible(true);

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            insertClassroomButton();
        });

    }


    public void insertClassroomButton() {

        checkFormValidationForClassroom();

        // Getting given by user values
//        Integer sid = Integer.valueOf(textField1AddForm.getText());
        String classroom_name = field7.getText();
        String classGrade = combobox1.getValue();
        String orientation = combobox2.getSelectionModel().getSelectedItem();

        // insert classroom
        try {
            String sqlINSERT = "select * from insertClassroom(?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, classroom_name);
            preparedStatement.setString(2, classGrade);
            preparedStatement.setString(3, orientation);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field6.setText("");
            field6.setDisable(false);
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("All Classes");
            combobox2.setPromptText("All Orientations");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Successfully Message");
            infoAlert.setHeaderText("Congratulations,");
            infoAlert.setContentText("Classroom named " + classroom_name + " added in our school!");
            infoAlert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeClassroomsUpdate(int id) throws SQLException {
        classesList = TableViewController.classesList;
        orientationClassesList = TableViewController.orientationClassesList;

        System.out.println("controllers.InsertUpdateController.initializeClassrooms orientationClassesList[] :");
        for (int i = 0; i < orientationClassesList.size(); i++) {
            System.out.println(i + " --> " + orientationClassesList.get(i));
        }

        System.out.println("controllers.InsertUpdateController.initializeClassrooms classesList[] :");
        for (int i = 0; i < classesList.size(); i++) {
            System.out.println(i + " --> " + classesList.get(i));
        }

        combobox1.getItems().addAll(classesList);
        combobox2.getItems().addAll(orientationClassesList);

        // Set label texts
        btn.setText("UPDATE CLASSROOM");
        titleLabel.setText("Edit Classroom");
        label6.setText("Classroom ID");
        label7.setText("Classroom Name");
        combobox1.setPromptText("All Classes");
        combobox2.setPromptText("All Orientations");

        // Modify visibility and edit-ability
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        label4.setVisible(false);
        label5.setVisible(false);
        label6.setVisible(true);
        label7.setVisible(true);

        field1.setVisible(false);
        field2.setVisible(false);
        field3.setVisible(false);
        field4.setVisible(false);
        field5.setVisible(false);
        field6.setVisible(true);
        field6.setDisable(true);
        field7.setVisible(true);

        // Setting fields values according to id
        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getClassroom(?)");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            field6.setText(String.valueOf(resultSet.getInt(1)));
            field7.setText(String.valueOf(resultSet.getString(2)));
            combobox1.setValue(String.valueOf(resultSet.getString(3)));
            combobox2.setValue(String.valueOf(resultSet.getString(4)));
        }

        preparedStatement.close();
        resultSet.close();

        // Handler for the button Onaction event
        btn.setOnAction(event -> {
            try {
                updateClassroomButton();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateClassroomButton() throws ParseException {

        checkFormValidationForClassroom();

        // Getting given by user values
        Integer classroomID = Integer.valueOf(field6.getText());
        String classroom_name = field7.getText();
        String classGrade = combobox1.getValue();
        String orientation = combobox2.getSelectionModel().getSelectedItem();

        // Update classroom
        try {

            String sqlINSERT = "select * from updateClassroom(?,?,?,?)";

            PreparedStatement preparedStatement = dbConnection.prepareStatement(sqlINSERT);
            preparedStatement.setString(1, classroom_name);
            preparedStatement.setString(2, classGrade);
            preparedStatement.setString(3, orientation);
            preparedStatement.setInt(4, classroomID);

            preparedStatement.executeQuery();
            preparedStatement.close();

            // Clearing all fields
            field6.setText("");
            field6.setDisable(false);
            field7.setText("");
            combobox1.getSelectionModel().selectFirst();
            combobox2.getSelectionModel().selectFirst();
            combobox1.setPromptText("All Classes");
            combobox2.setPromptText("All Orientations");

            // Successfully message alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Successfully Message");
            infoAlert.setHeaderText("Well,");
            infoAlert.setContentText("Classroom named " + classroom_name + " has been updated!");
            infoAlert.showAndWait();

            Stage stage = (Stage) btn.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //-------- VALIDATION CHECK --------\\
    private void checkFormValidationForStudent() throws ParseException {
        try {
            if (field2.getText().isEmpty() || field3.getText().isEmpty()) {
                throw new Exception("Textfield1 or Textfield2 length is 0 exception thrown");

            } else if (combobox1.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Gender combobox index is 0 exception thrown");

            } else if (field4.getEditor().getText().isBlank()) {
                throw new Exception("Datepicker1 null exception thrown");

            } else if (!field4.getEditor().getText().matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                System.out.println("Birth date field must be in the appropriate form: dd/mm/yyyy");
                throw new Exception("Birth date exception thrown");

            } else if (combobox2.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Classroom_id index is 0 exception thrown");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Every field which contains the * must be filled");
        }
    }

    private void checkFormValidationForTeacher() throws ParseException {
        try {
            if (field2.getText().isEmpty() || field3.getText().isEmpty()) {
                throw new Exception("Textfield1 or Textfield2 length is 0 exception thrown");

            } else if (combobox1.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Gender combobox index is 0 exception thrown");

            } else if (combobox2.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Classroom_id index is 0 exception thrown");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Every field which contains the * must be filled");
        }
    }

    private void checkFormValidationForCourse() throws ParseException {
        try {
            if (field2.getText().isEmpty() || field3.getText().isEmpty() || field5.getText().isEmpty()) {
                throw new Exception("Textfield1 or Textfield2 length is 0 exception thrown");

            } else if (combobox1.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Class combobox index is 0 exception thrown");

            } else if (combobox2.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Orientation index is 0 exception thrown");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Every field which contains the * must be filled");
        }
    }


    public void checkFormValidationForGrade() throws RuntimeException {
        try {
            if (field6.getText().isBlank()) {
                throw new Exception(" Textfield7 length is 0 exception thrown");

            } else if (combobox1.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Course combobox index is 0 exception thrown");

            } else if (combobox2.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Semester combobox index is 0 exception thrown");

            }
        } catch (Exception e) {
            System.out.println("Every field which contains the * must be filled");

        }
    }


    public void checkFormValidationForClassroom() throws RuntimeException {
        try {
            if (field7.getText().isBlank()) {
                throw new Exception("Textfield1 or Textfield2 length is 0 exception thrown");

            } else if (combobox1.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Classes combobox index is 0 exception thrown");

            } else if (combobox2.getSelectionModel().getSelectedIndex() == 0) {
                throw new Exception("Orientation combobox index is 0 exception thrown");

            }
        } catch (Exception e) {
            System.out.println("Every field which contains the * must be filled");

        }
    }

    @FXML
    private void initialize() {
        dbConnection = DBConnectionController.getDbConnection();

    }
}