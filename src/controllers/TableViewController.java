package controllers;

import entities.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableViewController {

    public static List<String> classroomsList, genderStudentList, genderTeacherList, specialtyTeacherList, classesList, orientationsList, teachersList, orientationClassesList, courseCodeList;
    public static List<Integer> semesterList;
    private static Connection dbConnection = null;

    @FXML
    private AnchorPane filterPane, tablePane, detailsPane, backgroundPane;

    @FXML
    private TextField searchbar;

    private InsertUpdateController insertUpdateController;

    @FXML
    private ComboBox<String> filter1, filter2, filter3;
    @FXML
    private Button exportBtn, addBtn, editBtn, deleteBtn;
    @FXML
    private Label detailsTitle, tableName, fieldTitle1, fieldTitle2, fieldTitle3, fieldTitle4, fieldTitle5, fieldTitle6, fieldTitle7, fieldTitle8, fieldTitle9, fieldTitle10, fieldTitle11;
    @FXML
    private Label textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11;
    @FXML
    private TextArea textArea1;
    @FXML
    private AnchorPane anchorPaneInsideScrollpane;
    @FXML
    private TableView tableView;


    public void showSelectedStudentData() {

        Student student = (Student) tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();

        int sid = student.getStudent_id();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getStudentDetails(?)");
            preparedStatement.setInt(1, sid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                textField1.setText(String.valueOf(resultSet.getString(1)));
                textField2.setText(String.valueOf(resultSet.getString(2)));
                textField3.setText(String.valueOf(resultSet.getString(4)));
                textField4.setText(String.valueOf(resultSet.getDate(3)));
                textField5.setText(String.valueOf(resultSet.getString(5)));
                textField6.setText(String.valueOf(resultSet.getString(6)));
                textField7.setText(String.valueOf(resultSet.getString(7)));
                textField8.setText(String.valueOf(resultSet.getString(8)));
                textField9.setText(String.valueOf(resultSet.getDouble(9)));
                textField10.setText(String.valueOf(resultSet.getDate(10)));
                textField11.setText(String.valueOf(resultSet.getTimestamp(11)));

            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void showSelectedTeacherData() {

        Teacher teacher = (Teacher) tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();

        int tid = teacher.getTeacher_id();
        PreparedStatement preparedStatement = null;

        // Get selected teacher data from tableview
        try {
            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getTeacherDetails(?)");
            preparedStatement.setInt(1, tid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                textField1.setText(String.valueOf(resultSet.getInt(1)));
                textField2.setText(String.valueOf(resultSet.getString(2)));
                textField3.setText(String.valueOf(resultSet.getString(3)));
                textField4.setText(String.valueOf(resultSet.getString(4)));

                String teachesText = String.valueOf(resultSet.getString(5));
                int characterLimit = 27;

                    if (teachesText.length() > characterLimit) {
                        // Insert a newline character (\n) at the desired index
                        int indexOfText = characterLimit;
                        teachesText = teachesText.substring(0, indexOfText) + "\n" + teachesText.substring(indexOfText);

                        // Set the modified text back to the label
                        textField5.setText(teachesText);
                    }else if(teachesText.equals("null")){
                        textField5.setText("-");
                    }else{  // teachesText is less than or equal to characterLimit
                    // Insert the given teachesText
                    textField5.setText(teachesText);
                }

                textField6.setText(String.valueOf(resultSet.getString(6)));
                textField7.setText(String.valueOf(resultSet.getString(7)));
                textField8.setText(String.valueOf(resultSet.getString(8)));
                textField9.setText(String.valueOf(resultSet.getTimestamp(9)));

            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void showSelectedCourseData() {

        Course course = (Course) tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();

        int cid = course.getCourse_id();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement("select * from getCourseDetails(?)");
            preparedStatement.setInt(1, cid);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                textField1.setText(String.valueOf(resultSet.getInt(1)));
                textField2.setText(String.valueOf(resultSet.getString(2)));
                textField3.setText(String.valueOf(resultSet.getString(3)));
                textField4.setText(String.valueOf( resultSet.getInt(4)));
                textField5.setText(String.valueOf(resultSet.getString(5)));
                textField6.setText(String.valueOf( resultSet.getString(6)));
                textArea1.setText(String.valueOf( resultSet.getString(7)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if ((index - 1) < -1) return;

    }

    public void showSelectedGradeData() {

        Grade grade = (Grade) tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();

        int sid = grade.getStudent_id();
        String cid = grade.getCourse_code();
        int sem = grade.getSemester();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement("select * from getGradeDetails(?,?,?)");
            preparedStatement.setInt(1, sid);
            preparedStatement.setString(2, cid);
            preparedStatement.setInt(3, sem);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                textField1.setText(String.valueOf(resultSet.getInt(1)));
                textField2.setText(String.valueOf(resultSet.getString(2)));
                textField3.setText(String.valueOf(resultSet.getString(3)));
                textField4.setText(String.valueOf( resultSet.getString(4)));
                textField5.setText(String.valueOf(resultSet.getString(5)));
                textField6.setText(String.valueOf( resultSet.getInt(6)));
                textField7.setText(String.valueOf( resultSet.getDouble(7)));
                textField8.setText(String.valueOf( resultSet.getTimestamp(8)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if ((index - 1) < -1) return;

    }

    public void showSelectedClassroomData() {

        Classroom classroom = (Classroom) tableView.getSelectionModel().getSelectedItem();
//        Integer index = tableView.getSelectionModel().getSelectedIndex();

        int classroomID = classroom.getClassroom_id();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getClassroomDetails(?)");
            preparedStatement.setInt(1, classroomID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                textField1.setText(String.valueOf(resultSet.getInt(1)));
                textField2.setText(String.valueOf(resultSet.getString(2)));
                textField3.setText(String.valueOf(resultSet.getString(3)));
                textField4.setText(String.valueOf(resultSet.getString(4)));
                textField5.setText(String.valueOf(resultSet.getInt(5)));

            }
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeTeachers() {

        tableView.setOnMouseClicked(mouseEvent -> {
            showSelectedTeacherData();
        });

        addBtn.setText("ADD TEACHER");
        tableName.setText("Teachers");
        detailsTitle.setText("Teacher Details");
        fieldTitle1.setText("TID");
        fieldTitle2.setText("Teacher Name");
        fieldTitle3.setText("Teacher Gender");
        fieldTitle4.setText("Specialty");
        fieldTitle5.setText("Teaches");
        fieldTitle6.setText("Email");
        fieldTitle7.setText("Phone");
        fieldTitle8.setText("Address");
        fieldTitle9.setText("Last Modification Timestamp");
//        fieldTitle10.setText("Registration Date");
        fieldTitle10.setVisible(false);
        fieldTitle11.setVisible(false);
        textField11.setVisible(false);
        textField10.setVisible(false);

        filter1.setPromptText("All Genders");
        filter2.setPromptText("All Specialties");
        filter3.setVisible(false);
        anchorPaneInsideScrollpane.setMaxSize(387, 780);

        genderTeacherList = new ArrayList<>();
        genderTeacherList.add(0, "All Genders");


        specialtyTeacherList = new ArrayList<>();
        specialtyTeacherList.add(0, "All Specialties");

        try {

            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT getTeacherGenders()");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                genderTeacherList.add(resultSet.getString(1));
            }


            filter1.getItems().addAll(genderTeacherList);
            filter1.setValue("All Genders");

            preparedStatement = dbConnection.prepareStatement("SELECT getTeacherSpecialties()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                specialtyTeacherList.add(resultSet.getString(1));
            }


            filter2.getItems().addAll(specialtyTeacherList);
            filter2.setValue("All Specialties");


            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getTeachers(?, ?, ?)");

            preparedStatement.setString(1, "All Genders");
            preparedStatement.setString(2, "All Specialties");
            preparedStatement.setString(3, "");
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();

            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                TableColumn column = new TableColumn(columnName.toUpperCase());
                column.setCellValueFactory(new PropertyValueFactory<>(columnName));
                tableView.getColumns().add(column);

            }

            preparedStatement.close();
            resultSet.close();

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

        loadTeachersTable();

        filter1.setOnAction(actionEvent -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter1.getSelectionModel().getSelectedItem();
            filter1.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter1.isShowing()); // Check if the ComboBox is open (user interaction)

            loadTeachersTable();
        });

        filter2.setOnAction(actionEvent -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter2.getSelectionModel().getSelectedItem();
            filter2.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter2.isShowing()); // Check if the ComboBox is open (user interaction)

            loadTeachersTable();
        });

        searchbar.setOnKeyReleased(keyEvent -> {
            // Check the new search text
//            String searchbarText = searchbar.getText();
//            System.out.println("Seach bar context: " + searchbarText); // Check if the ComboBox is open (user interaction)

            loadTeachersTable();
        });

    }

    public void initializeStudents() {

        tableView.setOnMouseClicked(mouseEvent -> {
            showSelectedStudentData();
        });

        addBtn.setText("ADD STUDENT");
        tableName.setText("Students");
        detailsTitle.setText("Student Details");
        fieldTitle1.setText("SID");
        fieldTitle2.setText("Student Name");
        fieldTitle3.setText("Student Gender");
        fieldTitle4.setText("Date of Birth");
        fieldTitle5.setText("Classroom");
        fieldTitle6.setText("Email");
        fieldTitle7.setText("Phone");
        fieldTitle8.setText("Address");
        fieldTitle9.setText("Average Grades");
        fieldTitle10.setText("Registration Date");
        fieldTitle11.setText("Last Modification Timestamp");
        filter1.setPromptText("Gender");
        filter2.setPromptText("Classroom");
        filter3.setPromptText("Average Grade");
        anchorPaneInsideScrollpane.setMaxSize(387, 950);

        classroomsList = new ArrayList<>();
        classroomsList.add(0, "All Classrooms");

        genderStudentList = new ArrayList<>();
        genderStudentList.add(0, "All Genders");

        List<String> gradeRangeList = new ArrayList<>();
        filter3.getItems().addAll("Average Grade", "0-7.99", "8-11.99", "12-15.99", "16-20");
//        filter3.getItems().addAll("Average Grade", "0-8", "8-12", "12-16", "16-20");

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT getClassroomNames()");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                classroomsList.add(resultSet.getString(1));
            }

            filter2.getItems().addAll(classroomsList);

            preparedStatement = dbConnection.prepareStatement("SELECT getStudentGenders()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                genderStudentList.add(resultSet.getString(1));
            }

            filter1.getItems().addAll(genderStudentList);

            filter1.setValue("All Genders");
            filter2.setValue("All Classrooms");
            filter3.setValue("Average Grade");


            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getStudents(?,?,?,?)");

            preparedStatement.setString(1, "All Genders");
            preparedStatement.setString(2, "All Classrooms");
            preparedStatement.setString(3, "Average Grade");
            preparedStatement.setString(4, "");
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();

            // Extract Metadata
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

//            for (int i = 1; i <= columnCount; i++) {
//                String label = metadata.getColumnLabel(i);
//                int jdbcType = metadata.getColumnType(i);
//                String DbmsType = metadata.getColumnTypeName(i);
//                String Typeclassname = metadata.getColumnClassName(i);
//                System.out.print("" + i + ". The column with name " + label);
//                System.out.print(" has JDBC type " + jdbcType);
//                System.out.print(", DBMS type " + DbmsType);
//                System.out.println("and classtype " + Typeclassname);
//            }

            // Create TableView Columns
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                TableColumn column = new TableColumn(columnName.toUpperCase());
                column.setCellValueFactory(new PropertyValueFactory<>(columnName));
                tableView.getColumns().add(column);

            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadStudentsTable();

        tableView.setOnMouseClicked(mouseEvent -> {
            showSelectedStudentData();
        });

        filter1.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter1.getSelectionModel().getSelectedItem();
            filter1.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter1.isShowing()); // Check if the ComboBox is open (user interaction)
            loadStudentsTable();

        });

        filter2.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter2.getSelectionModel().getSelectedItem();
            filter2.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter2.isShowing()); // Check if the ComboBox is open (user interaction)

            loadStudentsTable();
        });

        filter3.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter3.getSelectionModel().getSelectedItem();
            filter3.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter3.isShowing()); // Check if the ComboBox is open (user interaction)

            loadStudentsTable();
        });

        searchbar.setOnKeyReleased(keyEvent -> {
            // Check the changed search text
//            String searchbarText = searchbar.getText();
            //    System.out.println("Search bar context: " + searchbarText); // Check if the ComboBox is open (user interaction)

            loadStudentsTable();
        });

    }

    public void loadStudentsTable() {

        //tableView.getColumns().clear();

        String gender;
        String classroomName;

        gender = (String) filter1.getSelectionModel().getSelectedItem();
        classroomName = (String) filter2.getSelectionModel().getSelectedItem();

        String gradeRange = filter3.getValue();
        String searchBarText = searchbar.getText();

        // Execute Stored Procedure
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getStudents(?,?,?,?)");

            preparedStatement.setString(1, gender);
            preparedStatement.setString(2, classroomName);
            preparedStatement.setString(3, gradeRange);
            preparedStatement.setString(4, searchBarText);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            ObservableList<Student> listOfStudents = FXCollections.observableArrayList();

            Student studentData;
            while (resultSet.next()) {
                studentData = new Student(

                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDate(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getDouble(9));

                listOfStudents.add(studentData);
            }
            preparedStatement.close();
            resultSet.close();
            tableView.setItems(listOfStudents);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void loadTeachersTable() {

        //tableView.getColumns().clear();

        String gender;
        String specialty;
        String searchFilter;

        gender = (String) filter1.getSelectionModel().getSelectedItem();
        specialty = (String) filter2.getSelectionModel().getSelectedItem();
        searchFilter = (String) searchbar.getText();
        // Execute Stored Procedure
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getTeachers(?,?,?)");

            preparedStatement.setString(1, gender);
            preparedStatement.setString(2, specialty);
            preparedStatement.setString(3, searchFilter);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            ObservableList<Teacher> listOfTeachers = FXCollections.observableArrayList();

            Teacher teacherData;
            while (resultSet.next()) {
                teacherData = new Teacher(

                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7));

                listOfTeachers.add(teacherData);
            }
            preparedStatement.close();
            resultSet.close();
            tableView.setItems(listOfTeachers);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void loadCoursesTable() {

        //tableView.getColumns().clear();

        String class_grade, orientation, teacher;

        class_grade = (String) filter1.getSelectionModel().getSelectedItem();
        orientation = (String) filter2.getSelectionModel().getSelectedItem();
        teacher = (String) filter3.getSelectionModel().getSelectedItem();
        String searchBarText = searchbar.getText();

        // Execute Stored Procedure
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getCourses(?,?,?,?)");

            preparedStatement.setString(1, class_grade);
            preparedStatement.setString(2, orientation);
            preparedStatement.setString(3, teacher);
            preparedStatement.setString(4, searchBarText);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            ObservableList<Course> listOfCourses = FXCollections.observableArrayList();

            Course courseData;
            while (resultSet.next()) {
                courseData = new Course(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7));

                listOfCourses.add(courseData);
            }
            preparedStatement.close();
            resultSet.close();
            tableView.setItems(listOfCourses);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void loadGradesTable() {

        //tableView.getColumns().clear();

        String classroom, course;
        int semester;

        classroom = (String) filter1.getSelectionModel().getSelectedItem();
        course = (String) filter2.getSelectionModel().getSelectedItem();
        try {
            // Try to parse the string as an integer
            semester = Integer.parseInt(filter3.getSelectionModel().getSelectedItem());
        } catch (NumberFormatException e) {
            // If parsing fails, return a default value or handle it in a specific way
            semester = 0;
        }
        String searchBarText = searchbar.getText();


        // Execute Stored Procedure
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getGrades(?,?,?,?)");

            preparedStatement.setString(1, classroom);
            preparedStatement.setString(2, course);
            preparedStatement.setInt(3, semester);
            preparedStatement.setString(4, searchBarText);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            ObservableList<Grade> listOfGrades = FXCollections.observableArrayList();

            Grade gradedata;
            while (resultSet.next()) {
                gradedata = new Grade(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getInt(6),
                        resultSet.getDouble(7),
                        resultSet.getTimestamp(8));

                listOfGrades.add(gradedata);
            }
            preparedStatement.close();
            resultSet.close();
            tableView.setItems(listOfGrades);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadClassroomsTable() {

        //tableView.getColumns().clear();

        String className;
        String classOrientation;

        className = (String) filter1.getSelectionModel().getSelectedItem();
        classOrientation = (String) filter2.getSelectionModel().getSelectedItem();

        // Execute Stored Procedure
        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getClassrooms(?,?)");

            preparedStatement.setString(1, className);
            preparedStatement.setString(2, classOrientation);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            ObservableList<Classroom> listOfClassrooms = FXCollections.observableArrayList();

            Classroom classroomData;
            while (resultSet.next()) {
                classroomData = new Classroom(

                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4));

                listOfClassrooms.add(classroomData);
            }
            preparedStatement.close();
            resultSet.close();
            tableView.setItems(listOfClassrooms);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    @FXML
    private void switchToInsertUpdateForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/insertUpdateForm.fxml"));
            Parent root = loader.load();

            Stage secondaryStage = new Stage();
            secondaryStage.initModality(Modality.APPLICATION_MODAL);
            secondaryStage.initOwner(addBtn.getScene().getWindow());
            secondaryStage.setResizable(false);
            secondaryStage.setScene(new Scene(root));

            Button clickedButton = (Button) event.getSource();
            String buttonId = clickedButton.getId();

            switch (buttonId) {
                case "addBtn":
                    insertUpdateController = loader.getController();

                    switch (detailsTitle.getText().toUpperCase()) {
                        case "STUDENT DETAILS":
                            secondaryStage.setTitle("Add Student");
                            insertUpdateController.initializeStudents();
                            secondaryStage.showAndWait();
                            loadStudentsTable();
                            break;
                        case "TEACHER DETAILS":
                            secondaryStage.setTitle("Add Teacher");
                            insertUpdateController.initializeTeachers();
                            secondaryStage.showAndWait();
                            loadTeachersTable();
                            break;
                        case "COURSE DETAILS":
                            secondaryStage.setTitle("Add Course");
                            insertUpdateController.initializeCourses();
                            secondaryStage.showAndWait();
                            loadCoursesTable();
                            break;
                        case "GRADE DETAILS":
                            secondaryStage.setTitle("Add Grade");
                            insertUpdateController.initializeGrades();
                            secondaryStage.showAndWait();
                            loadGradesTable();
                            break;
                        case "CLASSROOM DETAILS":
                            secondaryStage.setTitle("Add Classroom");
                            insertUpdateController.initializeClassrooms();
                            secondaryStage.showAndWait();
                            loadClassroomsTable();
                            break;
                        default:
                            // Handle other cases
                    }
                    break;
                case "editBtn":
                    int index = tableView.getSelectionModel().getSelectedIndex();
                    System.out.println("controllers.TableViewController.switchToInsertUpdateForm tableview index: " +index);
                    if(index == -1) {
                        return;
                    } // if no selected data of tableview then do return

                    insertUpdateController = loader.getController();
                    int id;

                    switch (detailsTitle.getText().toUpperCase()) {
                        case "STUDENT DETAILS":
                            secondaryStage.setTitle("Update Student");
                            Student student = (Student) tableView.getSelectionModel().getSelectedItem();
                            id = student.getStudent_id();
                            insertUpdateController.initializeStudentsUpdate(id);
                            secondaryStage.showAndWait();
                            loadStudentsTable();
                            break;
                        case "TEACHER DETAILS":
                            secondaryStage.setTitle("Update Teacher");
                            Teacher teacher = (Teacher) tableView.getSelectionModel().getSelectedItem();
                            id = teacher.getTeacher_id();
                            insertUpdateController.initializeTeachersUpdate(id);
                            secondaryStage.showAndWait();
                            loadTeachersTable();
                            break;
                        case "COURSE DETAILS":
                            secondaryStage.setTitle("Update Course");
                            Course course = (Course) tableView.getSelectionModel().getSelectedItem();
                            id = course.getCourse_id();
                            insertUpdateController.initializeCoursesUpdate(id);
                            secondaryStage.showAndWait();
                            loadCoursesTable();
                            break;
                        case "GRADE DETAILS":
                            secondaryStage.setTitle("Update grade");
                            Grade grade = (Grade) tableView.getSelectionModel().getSelectedItem();
                            int sid = grade.getStudent_id();
                            String cid = grade.getCourse_code();
                            int sem = grade.getSemester();
                            insertUpdateController.initializeGradesUpdate(sid, cid, sem);
                            secondaryStage.showAndWait();
                            loadGradesTable();
                            break;
                        case "CLASSROOM DETAILS":
                            Classroom classroom = (Classroom) tableView.getSelectionModel().getSelectedItem();
                            id = classroom.getClassroom_id();
                            secondaryStage.setTitle("Update Classroom");
                            insertUpdateController.initializeClassroomsUpdate(id);
                            secondaryStage.showAndWait();
                            loadClassroomsTable();
                            break;
                        default:
                            // Handle other cases
                    }
                    break;
                default:
                    // Handle other cases
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void actionDelete() {

//        int index = tableView.getSelectionModel().getSelectedIndex();
//
//        if (index == -1) {
//            return;
//        } // if no selected data of tableview then do return
//        int id;
//        Alert warningAlert;
//        ButtonType result;
//        String full_name;

        switch (detailsTitle.getText().toUpperCase()) {
            case "STUDENT DETAILS":
                Student student = (Student) tableView.getSelectionModel().getSelectedItem();

                if (student == null) {
                    return;
                } // if student is null then return
                System.out.println("controllers.TableViewController.actionDelete student you want to delete is null");

                int id = student.getStudent_id();
                String studentName = student.getStudent_name();

                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Delete Warning");
                warningAlert.setHeaderText("Are you sure?");
                warningAlert.setContentText("Student with id: " + id + ", named " + studentName + " will be permanently deleted!");
                warningAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                // Show the alert and wait for the user's response
                ButtonType result = warningAlert.showAndWait().orElse(ButtonType.CANCEL);

                // Check the user's response
                if (result == ButtonType.OK) {
                    // User pressed OK, perform the desired action
                    try {
                        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM deleteStudent(?)");
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeQuery();
                        preparedStatement.close();

                        // refresh students table
                        loadStudentsTable();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            case "TEACHER DETAILS":
                Teacher teacher = (Teacher) tableView.getSelectionModel().getSelectedItem();
                id = teacher.getTeacher_id();
                String full_name = teacher.getTeacher_full_name();

                 warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Warning Message");
                warningAlert.setHeaderText("Carefully,");
                warningAlert.setContentText("Teacher named " + full_name + " will be permanently deleted!");

                warningAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                // Show the alert and wait for the user's response
                result = warningAlert.showAndWait().orElse(ButtonType.CANCEL);

                // Check the user's response
                if (result == ButtonType.OK) {
                    // User pressed OK, perform the desired action
                    try {
                        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM deleteTeacher(?)");
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeQuery();
                        preparedStatement.close();

                        // refresh teachers tableview
                        loadTeachersTable();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            case "COURSE DETAILS":
                Course course = (Course) tableView.getSelectionModel().getSelectedItem();

                if (course == null) {
                    return;
                } // if student is null then return
                System.out.println("controllers.TableViewController.actionDelete course you want to delete is null");

                id = course.getCourse_id();
                String courseTitle = course.getTitle();

                warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Delete Warning");
                warningAlert.setHeaderText("Are you sure?");
                warningAlert.setContentText("Course with id: " + id + ", with title: " + courseTitle + " will be permanently deleted!");
                warningAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                // Show the alert and wait for the user's response
                result = warningAlert.showAndWait().orElse(ButtonType.CANCEL);

                // Check the user's response
                if (result == ButtonType.OK) {
                    // User pressed OK, perform the desired action
                    try {
                        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM deleteCourse(?)");
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeQuery();
                        preparedStatement.close();

                        // refresh students table
                        loadCoursesTable();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                break;
            case "GRADE DETAILS":
                Grade grade = (Grade) tableView.getSelectionModel().getSelectedItem();

                if (grade == null) {
                    return;
                } // if student is null then return
                System.out.println("controllers.TableViewController.actionDelete course you want to delete is null");

                int sid = grade.getStudent_id();
                String cid = grade.getCourse_code();
                int sem = grade.getSemester();

                warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Delete Warning");
                warningAlert.setHeaderText("Are you sure?");
                warningAlert.setContentText("Grade of student with id: " + sid + " of semester: " +sem + " for course: " +cid+ " will be permanently deleted!");
                warningAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                // Show the alert and wait for the user's response
                result = warningAlert.showAndWait().orElse(ButtonType.CANCEL);

                // Check the user's response
                if (result == ButtonType.OK) {
                    // User pressed OK, perform the desired action
                    try {
                        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM deleteGrade(?,?,?)");
                        preparedStatement.setInt(1, sid);
                        preparedStatement.setString(2, cid);
                        preparedStatement.setInt(3, sem);
                        preparedStatement.executeQuery();
                        preparedStatement.close();

                        // refresh students table
                        loadGradesTable();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                break;
            case "CLASSROOM DETAILS":
                Classroom classroom = (Classroom) tableView.getSelectionModel().getSelectedItem();
                id = classroom.getClassroom_id();
                full_name = classroom.getClassroom_name();

                warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Warning Message");
                warningAlert.setHeaderText("Carefully,");
                warningAlert.setContentText("Classroom called " + full_name + " will be permanently deleted!");

                warningAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                // Show the alert and wait for the user's response
                result = warningAlert.showAndWait().orElse(ButtonType.CANCEL);

                // Check the user's response
                if (result == ButtonType.OK) {
                    // User pressed OK, perform the desired action
                    try {
                        PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM deleteClassroom(?)");
                        preparedStatement.setInt(1, id);
                        preparedStatement.executeQuery();
                        preparedStatement.close();

                        // refresh teachers tableview
                        loadClassroomsTable();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            default:
                // Handle other cases
        }
    }

    public void initializeCourses() {
        addBtn.setText("ADD COURSE");
        tableName.setText("Courses");
        detailsTitle.setText("Course Details");
        fieldTitle1.setText("CID");
        fieldTitle2.setText("Course Name");
        fieldTitle3.setText("Course Code");
        fieldTitle4.setText("Hours per week");
        fieldTitle5.setText("Class");
        fieldTitle6.setText("Orientation");
        fieldTitle7.setText("Description");
        fieldTitle8.setVisible(false);
        fieldTitle9.setVisible(false);
        fieldTitle10.setVisible(false);
        fieldTitle11.setVisible(false);
        textField7.setVisible(false);
        textField8.setVisible(false);
        textField9.setVisible(false);
        textField10.setVisible(false);
        textField11.setVisible(false);
        filter1.setPromptText("Class");
        filter2.setPromptText("Orientation");
        filter3.setPromptText("Teacher");
        anchorPaneInsideScrollpane.setMaxSize(385,780);
        //textField6.setMaxSize(200,500);
        textArea1.setVisible(true);
        textArea1.setMaxSize(300,800);
        textArea1.setWrapText(true);


        classesList = new ArrayList<>();
        classesList.add(0, "All Classes");


        orientationsList = new ArrayList<>();
        orientationsList.add(0, "All Orientations");

        teachersList = new ArrayList<>();
        teachersList.add(0, "All Teachers");


        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT getClasses()");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                classesList.add(resultSet.getString(1));
            }

            filter1.getItems().addAll(classesList);

            preparedStatement = dbConnection.prepareStatement("SELECT getOrientations()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orientationsList.add(resultSet.getString(1));
            }

            filter2.getItems().addAll(orientationsList);

            preparedStatement = dbConnection.prepareStatement("SELECT getTeacherNames()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                teachersList.add(resultSet.getString(1));
            }

            filter3.getItems().addAll(teachersList);

            filter1.setValue("All Classes");
            filter2.setValue("All Orientations");
            filter3.setValue("All Teachers");



            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getCourses(?,?,?,?)");

            preparedStatement.setString(1, "All Classes");
            preparedStatement.setString(2, "All Orientations");
            preparedStatement.setString(3, "All Teachers");
            preparedStatement.setString(4,"");
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();

            // Extract Metadata
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

            // Create TableView Columns
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                TableColumn column = new TableColumn(columnName.toUpperCase());
                column.setCellValueFactory(new PropertyValueFactory<>(columnName));
                tableView.getColumns().add(column);

            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadCoursesTable();

        tableView.setOnMouseClicked(mouseEvent -> {
            showSelectedCourseData();
        });

        filter2.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter2.getSelectionModel().getSelectedItem();
            filter2.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter2.isShowing()); // Check if the ComboBox is open (user interaction)

            loadCoursesTable();
        });

        filter1.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter1.getSelectionModel().getSelectedItem();
            filter1.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter1.isShowing()); // Check if the ComboBox is open (user interaction)

            loadCoursesTable();

        });


        filter3.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter3.getSelectionModel().getSelectedItem();
            filter3.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter3.isShowing()); // Check if the ComboBox is open (user interaction)

            loadCoursesTable();
        });

        searchbar.setOnKeyReleased(keyEvent -> {
            // Check the changed search text
//            String searchbarText = searchbar.getText();
            //    System.out.println("Search bar context: " + searchbarText); // Check if the ComboBox is open (user interaction)

            loadCoursesTable();
        });
    }

    public void initializeGrades() {

        tableView.setOnMouseClicked(mouseEvent -> {
            showSelectedGradeData();
        });

        addBtn.setText("ADD GRADE");
        tableName.setText("Grades");
        detailsTitle.setText("Grade Details");
        fieldTitle1.setText("SID");
        fieldTitle2.setText("Student Name");
        fieldTitle3.setText("Classroom Name");
        fieldTitle4.setText("Course Title");
        fieldTitle5.setText("Course Code");
        fieldTitle6.setText("Semester");
        fieldTitle7.setText("Grade");
        fieldTitle8.setText("Last Modified");
        fieldTitle9.setVisible(false);
        fieldTitle10.setVisible(false);
        fieldTitle11.setVisible(false);
        textField9.setVisible(false);
        textField10.setVisible(false);
        textField11.setVisible(false);
        filter1.setPromptText("Classroom");
        filter2.setPromptText("Course");
        filter3.setPromptText("Semester");
        anchorPaneInsideScrollpane.setMaxSize(387, 780);

        classroomsList = new ArrayList<>();
        classroomsList.add(0, "All Classrooms");

        courseCodeList = new ArrayList<>();
        courseCodeList.add(0, "All Courses");

        semesterList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT getClassroomNames()");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                classroomsList.add(resultSet.getString(1));
            }

            filter1.getItems().addAll(classroomsList);

            preparedStatement = dbConnection.prepareStatement("SELECT getCourseCodes()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                courseCodeList.add(resultSet.getString(1));
            }

            filter2.getItems().addAll(courseCodeList);

            preparedStatement = dbConnection.prepareStatement("SELECT getSemesters()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                semesterList.add(resultSet.getInt(1));
            }

            filter3.getItems().add(0,"All Semesters");
            filter3.getItems().addAll(
                    semesterList.stream()
                            .map(Object::toString)
                            .toList()
            );



            filter1.setValue("All Classrooms");
            filter2.setValue("All Courses");
            filter3.setValue("All Semesters");

            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getGrades(?,?,?,?)");

            preparedStatement.setString(1, "All Classrooms");
            preparedStatement.setString(2, "All Courses");
            preparedStatement.setInt(3, 0);
            preparedStatement.setString(4,"");
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();

            // Extract Metadata
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

            // Create TableView Columns
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                TableColumn column = new TableColumn(columnName.toUpperCase());
                column.setCellValueFactory(new PropertyValueFactory<>(columnName));
                tableView.getColumns().add(column);

            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadGradesTable();

        filter1.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter1.getSelectionModel().getSelectedItem();
            filter1.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter1.isShowing()); // Check if the ComboBox is open (user interaction)
            loadGradesTable();

        });

        filter2.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter2.getSelectionModel().getSelectedItem();
            filter2.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter2.isShowing()); // Check if the ComboBox is open (user interaction)

            loadGradesTable();
        });


        filter3.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter3.getSelectionModel().getSelectedItem();
            filter3.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter3.isShowing()); // Check if the ComboBox is open (user interaction)

            loadGradesTable();
        });

        searchbar.setOnKeyReleased(keyEvent -> {
            // Check the changed search text
//            String searchbarText = searchbar.getText();
            //    System.out.println("Search bar context: " + searchbarText); // Check if the ComboBox is open (user interaction)

            loadGradesTable();
        });
    }

    public void initializeClassrooms() {

        tableView.setOnMouseClicked(mouseEvent -> {
            showSelectedClassroomData();
        });

        addBtn.setText("ADD CLASSROOM");
        tableName.setText("Classrooms");
        detailsTitle.setText("Classroom Details");
        fieldTitle1.setText("Classroom ID");
        fieldTitle2.setText("Classroom Name");
        fieldTitle3.setText("Class");
        fieldTitle4.setText("Orientation");
        fieldTitle5.setText("Number of Students");
//        fieldTitle5.setVisible(false);
        fieldTitle6.setVisible(false);
        fieldTitle7.setVisible(false);
        fieldTitle8.setVisible(false);
        fieldTitle9.setVisible(false);
        fieldTitle10.setVisible(false);
        fieldTitle11.setVisible(false);
//        textField5.setVisible(false);
        textField6.setVisible(false);
        textField7.setVisible(false);
        textField8.setVisible(false);
        textField9.setVisible(false);
        textField10.setVisible(false);
        textField11.setVisible(false);
        filter1.setPromptText("Classroom");
        filter2.setPromptText("Orientation");
        filter3.setVisible(false);
        searchbar.setDisable(true);
        anchorPaneInsideScrollpane.setMaxSize(387, 780);

        classesList = new ArrayList<>();
        classesList.add(0, "All Classes");

        orientationClassesList = new ArrayList<>();
        orientationClassesList.add(0, "All Orientations");

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT getClasses()");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                classesList.add(resultSet.getString(1));
            }

            filter1.getItems().addAll(classesList);

            preparedStatement = dbConnection.prepareStatement("SELECT getOrientations()");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orientationClassesList.add(resultSet.getString(1));
            }

            filter2.getItems().addAll(orientationClassesList);

            filter1.setValue("All Classes");
            filter2.setValue("All Orientations");

            preparedStatement = dbConnection.prepareStatement("SELECT * FROM getClassrooms(?,?)");

            preparedStatement.setString(1, "All Classes");
            preparedStatement.setString(2, "All Orientations");
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();

            // Extract Metadata
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

            // Create TableView Columns
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                TableColumn column = new TableColumn(columnName.toUpperCase());
                column.setCellValueFactory(new PropertyValueFactory<>(columnName));
                tableView.getColumns().add(column);

            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadClassroomsTable();

        filter1.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter1.getSelectionModel().getSelectedItem();
            filter1.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter1.isShowing()); // Check if the ComboBox is open (user interaction)
            loadClassroomsTable();

        });

        filter2.setOnAction(event -> {
            // Check if the selected item has been changed
            String selectedItem = (String) filter2.getSelectionModel().getSelectedItem();
            filter2.setPromptText(selectedItem);
            System.out.println("Selected Item: " + selectedItem);
            System.out.println("Is changed by user: " + filter2.isShowing()); // Check if the ComboBox is open (user interaction)

            loadClassroomsTable();
        });

    }


    public void initializeHistory() {
        filterPane.setVisible(false);
        detailsPane.setVisible(false);
        tableName.setText("History");
        AnchorPane.setTopAnchor(backgroundPane, 14.0);
        AnchorPane.setRightAnchor(tablePane, 14.0);


        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement("SELECT * FROM getLog()");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Extract Metadata
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();

            // Create TableView Columns
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName.toUpperCase());
                final int columnIndex = i - 1; // Adjust index to start from 0
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex)));
                column.setPrefWidth(150);
                tableView.getColumns().add(column);
            }


            // Create an ObservableList to store data
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
               data.add(row);
            }
            tableView.setItems(data);

            preparedStatement.close();
            resultSet.close();

    }catch (SQLException e) {
        e.printStackTrace();
    }
    }


    @FXML
    private void initialize() {

        dbConnection = DBConnectionController.getDbConnection();

//        filter2.setOnAction(event -> {
//            // Check if the selected item has been changed
//            String selectedItem = (String) filter2.getSelectionModel().getSelectedItem();
//            filter2.setPromptText(selectedItem);
//            System.out.println("Selected Item: " + selectedItem);
//            System.out.println("Is changed by user: " + filter2.isShowing()); // Check if the ComboBox is open (user interaction)
//
//            loadStudentsTable();
//        });
//
//        filter1.setOnAction(event -> {
//            // Check if the selected item has been changed
//            String selectedItem = (String) filter1.getSelectionModel().getSelectedItem();
//            filter1.setPromptText(selectedItem);
//            System.out.println("Selected Item: " + selectedItem);
//            System.out.println("Is changed by user: " + filter1.isShowing()); // Check if the ComboBox is open (user interaction)
//            loadStudentsTable();
//
//        });
//
//        filter3.setOnAction(event -> {
//            // Check if the selected item has been changed
//            String selectedItem = (String) filter3.getSelectionModel().getSelectedItem();
//            filter3.setPromptText(selectedItem);
//            System.out.println("Selected Item: " + selectedItem);
//            System.out.println("Is changed by user: " + filter3.isShowing()); // Check if the ComboBox is open (user interaction)
//
//            loadStudentsTable();
//        });

    }
}
