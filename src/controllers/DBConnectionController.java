package controllers;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class DBConnectionController {

    @FXML
    private TextField dbHostField, dbPortField, dbNameField, dbSchemaField;

    @FXML
    private TextField dbUsernameField;

    @FXML
    private PasswordField dbPasswordField;

    @FXML
    private TextField sshHostField;

    @FXML
    private TextField sshPortField, localPortField;

    @FXML
    private TextField sshUsernameField;

    @FXML
    private PasswordField sshPasswordField;

    @FXML
    private CheckBox useSshCheckBox;

    @FXML
    private Button connectButton;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab sshTab, dbConnectTab;

    private static Session session = null;
    private static Connection dbConnection = null;

    public static Connection getDbConnection() {
        return dbConnection;
    }

    public static void closeConnection(){
        // Close the Database Connection when done
        try {
            if(dbConnection != null && !dbConnection.isClosed()){
                System.out.println("Closing Database Connection.");
                dbConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Close the SSH session when done
        if (session != null && session.isConnected()) {
            System.out.println("Closing SSH session.");
            session.disconnect();
        }
    }

    @FXML
    private void initialize() {
        // You can initialize components or perform setup here
        // For example, disable SSH components initially
        disableSshComponents(true);
    }


    // Show an alert for invalid input
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleUseSshCheckBox() {
        // Enable or disable SSH components based on checkbox state
        boolean isSshSelected = useSshCheckBox.isSelected();
        disableSshComponents(!isSshSelected);
    }

    @FXML
    private void handleConnectButton() throws SQLException {

        if (checkTextFields(dbHostField, dbPortField, dbUsernameField, dbPasswordField, dbNameField, dbSchemaField)) return;
        if(!isValidPortNumber(dbPortField))return;

        // Implement database connection logic here
        String dbHost = dbHostField.getText();
        int dbPort = Integer.parseInt(dbPortField.getText());
        String dbUsername = dbUsernameField.getText();
        String dbPassword = dbPasswordField.getText();
        String dbName = dbNameField.getText();
        String dbSchema = dbSchemaField.getText();

        // Perform the actual connection and other actions
        // For example, you can print the values to the console
        System.out.println("DB URL: " + dbHost);
        System.out.println("DB Port: " + dbPort);
        System.out.println("DB Username: " + dbUsername);
//        System.out.println("DB Password: " + dbPassword);
        System.out.println("DB Name: " + dbName);


        try {
            // Implement SSH connection logic if needed
            if (useSshCheckBox.isSelected()) {

                if (checkTextFields(sshHostField, sshUsernameField, sshPasswordField, sshPortField, localPortField, dbHostField, dbPortField, dbUsernameField, dbPasswordField, dbNameField))
                    return;
                if (!isValidPortNumber(sshPortField) || !isValidPortNumber(localPortField)) return;

                String sshHost = sshHostField.getText();
                String sshUsername = sshUsernameField.getText();
                String sshPassword = sshPasswordField.getText();
                int sshPort = Integer.parseInt(sshPortField.getText());
                int localPort = Integer.parseInt(localPortField.getText()); // Local port to use for the tunnel CAREFUL the port must not be used by other process in the system (5432 is often used for local database)


                // Implement SSH connection logic here
                System.out.println("SSH Host: " + sshHost);
                System.out.println("SSH Port: " + sshPort);
                System.out.println("SSH Username: " + sshUsername);
                //System.out.println("SSH Password: " + sshPassword);


                try {
                    // Create SSH session
                    JSch jsch = new JSch();
                    session = jsch.getSession(sshUsername, sshHost, sshPort);
                    session.setPassword(sshPassword);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();

                    // Set up port forwarding
                    session.setPortForwardingL(localPort, dbHost, dbPort);

                    System.out.println("SSH tunnel established on port " + localPort);

                    //Database Connection via ssh...

                    dbHost = "localhost";
                    dbPort = localPort;

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\n -- Exception -- \n");
                    System.out.println("Message: " + e.getMessage());
                    System.out.println("");
                    throw new SshConnectionException("SSH connection failed", e);
                }
            }

            dbConnection = connectToDatabase(dbHost, dbPort, dbUsername, dbPassword, dbName, dbSchema);

            // Create an information alert
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Information Dialog");
            infoAlert.setHeaderText("Welcome");
            infoAlert.setContentText("You are Connected to \"" +dbName+ "\" database");
            infoAlert.showAndWait();

            Stage dbConnectionStage = (Stage) tabPane.getScene().getWindow();
            dbConnectionStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sidebar.fxml"));

            if (loader.getLocation() == null) {
                // FXML file not found
                System.err.println("FXML file not found!");
                return;
            }
            Parent root = loader.load();

//            // Get the controller from the loader
//            controllers.TableViewController controller = loader.getController();
//            controller.initialize(dbConnection);

            // Pass data to the controller if needed (e.g., username)
            //controller.initialize(dbUsername);

            // Create a new stage and set the scene
            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root, 1920, 1080) );
            primaryStage.setTitle("School Management System");
            primaryStage.setResizable(false);

            primaryStage.setOnCloseRequest(event -> {

                // Show a confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Are you sure you want to close the application?");
                alert.setContentText("Any unsaved changes will be lost.");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // User clicked OK, close the database connection
                    closeConnection();
                } else {
                    // User clicked Cancel or closed the dialog, consume the event to prevent closing
                    event.consume();
                }
            });

            // Show the new stage
            primaryStage.show();


        }catch (SQLException e) {
            // Display an alert for connection failure
            showAlert("Database Connection Failed", "Failed to connect to the database. Check your connection details.");
            closeConnection();
            // Clear text fields
            clearTextFields(dbHostField, dbPortField, dbUsernameField, dbPasswordField, dbNameField);
            if (useSshCheckBox.isSelected()) {
                clearTextFields(sshHostField, sshUsernameField, sshPasswordField, sshPortField);
            }
            //e.printStackTrace(); // Print the exception for debugging purposes
        } catch (SshConnectionException e) {
            showAlert("SSH Connection Failed", "Failed to establish SSH connection. Check your connection details.");
            closeConnection();
            clearTextFields(dbHostField, dbPortField, dbUsernameField, dbPasswordField, dbNameField, sshHostField, sshUsernameField, sshPasswordField, sshPortField);
            //e.printStackTrace();
        } catch (Exception e) {
            closeConnection();
            // Handle other exceptions
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }  finally {

            }
    }


    public static Connection connectToDatabase(String host, int port, String username, String password, String dbName, String dbSchema) throws SQLException {
        String driverClassName = "org.postgresql.Driver";
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName + "?currentSchema=" + dbSchema;
        Connection connection = null;

        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to PostgreSQL database");
        } catch (SQLException ex) {
            System.out.println("\n -- SQL Exception -- \n");
            throw ex;
//            while (ex != null) {
//                System.out.println("Message: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("ErrorCode: " + ex.getErrorCode());
//                ex = ex.getNextException();
//                System.out.println("");
//            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver class not found: " + driverClassName);
        }

        return connection;
    }

    public static void clearTextFields(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.clear();
        }
    }

    private boolean checkTextFields(TextField... textFields) {
        boolean isEmpty = false;
        for (TextField textField : textFields) {
            if (textField.getText().isEmpty()) {
                isEmpty = true;
                //System.out.println("TextField is empty: " + textField.getId());
                // You can add your logic here for handling empty text fields
            }
        }
        if (isEmpty){
            showAlert("Empty Fields", "Please fill in all the fields");
            for (TextField textField : textFields) {
                textField.setText("");
            }
        }
        return isEmpty;
    }
    private boolean isValidPortNumber(TextField textField) {
        try {
            int port = Integer.parseInt(textField.getText());
            return port >= 0 && port <= 65535;
        } catch (NumberFormatException e) {
            // If not a valid integer, show an alert or handle it accordingly
            showAlert("Invalid Port Number", "Please enter a valid integer between 0 and 65535.");
            return false;
        }

    }

    private void disableSshComponents(boolean disable) {
        // Disable or enable SSH components
        sshHostField.setDisable(disable);
        sshPortField.setDisable(disable);
        sshUsernameField.setDisable(disable);
        sshPasswordField.setDisable(disable);
        localPortField.setDisable(disable);
    }


}

class SshConnectionException extends Exception {
    public SshConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
