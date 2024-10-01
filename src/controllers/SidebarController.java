package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class SidebarController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private AnchorPane centerPane, sidebar;

    @FXML
    private Button dashboardBtn, teachersBtn, studentsBtn, coursesBtn, gradesBtn, classroomsBtn, historyBtn;
    private Button selectedButton;

    private TableViewController tableViewController;
    private DashboardController dashboardController;

    @FXML
    private void loadTableView(ActionEvent event) {
        try {
            Button clickedButton = (Button) event.getSource();

            // Get the id of the clicked button
            String buttonId = clickedButton.getId();

            FXMLLoader loader;
            Parent root;

            switch (buttonId) {
                case "dashboardBtn":
                    loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                    root = loader.load();
                    mainPane.setCenter(root);

                    highlightButton(dashboardBtn);
                    dashboardController = loader.getController();
                    dashboardController.initializeDashboard();
                    break;
                case "teachersBtn":
                case "studentsBtn":
                case "coursesBtn":
                case "gradesBtn":
                case "classroomsBtn":
                case "historyBtn":
                    loader = new FXMLLoader(getClass().getResource("/views/tableView.fxml"));
                    root = loader.load();
                    mainPane.setCenter(root);

                    switch (buttonId) {
                        case "teachersBtn":
                            highlightButton(teachersBtn);
                            tableViewController = loader.getController();
                            tableViewController.initializeTeachers();
                            break;
                        case "studentsBtn":
                            highlightButton(studentsBtn);
                            tableViewController = loader.getController();
                            tableViewController.initializeStudents();
                            break;
                        case "coursesBtn":
                            highlightButton(coursesBtn);
                            tableViewController = loader.getController();
                            tableViewController.initializeCourses();
                            break;
                        case "gradesBtn":
                            highlightButton(gradesBtn);
                            tableViewController = loader.getController();
                            tableViewController.initializeGrades();
                            break;
                        case "classroomsBtn":
                            highlightButton(classroomsBtn);
                            tableViewController = loader.getController();
                            tableViewController.initializeClassrooms();
                            break;
                        case "historyBtn":
                            highlightButton(historyBtn);
                            tableViewController = loader.getController();
                            tableViewController.initializeHistory();
                            break;
                        default:
                            // Handle other cases if needed
                    }
                    break;
//                case "historyBtn":
//                    // Handle historyBtn case if needed
//                    break;
                default:
                    // Handle other cases if needed
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void highlightButton(Button selectedButton) {
        // Highlight the selected button
        if (this.selectedButton != null) {
            this.selectedButton.setStyle(""); // Clear previous style
        }

        selectedButton.setStyle("-fx-background-color: rgba(125,230,255,0.75); -fx-font-weight: bold;");
        this.selectedButton = selectedButton;
    }


    @FXML
    private void initialize() {
        FXMLLoader loader;
        Parent root;
        loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainPane.setCenter(root);

        highlightButton(dashboardBtn);
        dashboardController = loader.getController();
        dashboardController.initializeDashboard();

    }
}
