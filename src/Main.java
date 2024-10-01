import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/dbConnection-view.fxml"));

        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sidebar.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        primaryStage.setTitle("Database Connection");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}