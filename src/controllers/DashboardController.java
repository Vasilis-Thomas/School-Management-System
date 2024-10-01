package controllers;

import com.jcraft.jsch.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    @FXML
    private Label label1, label2, label3, text1, text2, text3;
    @FXML
    private Label labelOverallTitle;
    @FXML
    private Label labelOverall1, labelOverall2, labelOverall3, labelOverall4, labelDate;
    @FXML
    private Label textOverall1, textOverall2, textOverall3, textOverall4, textDate;
    @FXML
    private Button btnAbout, btnSupport;
    private static Session session = null;
    private static Connection dbConnection = null;

    public void initializeDashboard(){

        label1.setText("");
        label2.setText("");
        label3.setText("About");

        text1.setText("");
        text2.setText("");
        text3.setText("Learn about the creators of this incredible application!");

        labelDate.setText("Current Date: ");
        labelOverallTitle.setText("Total Stats");
        labelOverall1.setText("No of Students:");
        labelOverall2.setText("No of Teachers:");
        labelOverall3.setText("No of Courses:");
        labelOverall4.setText("No of Classrooms:");



//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));
//        textDate.setText(dtf.format(now));

        try {
        String sqlSelect = "SELECT * FROM getOverallStats()";
            PreparedStatement preparedStatement = (PreparedStatement) dbConnection.prepareStatement(sqlSelect);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){

                textOverall1.setText(String.valueOf(resultSet.getInt(1)));
                textOverall2.setText(String.valueOf(resultSet.getInt(2)));
                textOverall3.setText(String.valueOf(resultSet.getInt(3)));
                textOverall4.setText(String.valueOf(resultSet.getInt(4)));

            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }


    }

    @FXML
    private void initialize() {
        dbConnection = DBConnectionController.getDbConnection();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        textDate.setText(dtf.format(now));

        btnAbout.setOnAction(actionEvent -> {
            Alert InfoAlert = new Alert(Alert.AlertType.INFORMATION);
            InfoAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            InfoAlert.setTitle(" About");
            InfoAlert.setHeaderText("Application Info ");
            InfoAlert.setContentText("The application developed in terms of a student task\n"
                                   + "Developers: Sarakenidis Nikolas and Thomas Vasilis\n"
                                   + "Last Update: 23/12/2023\n"
                                   + "Version: 1.7"
            );
            InfoAlert.show();
//            InfoAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        });

    }

}
