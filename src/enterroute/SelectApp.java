package enterroute;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SelectApp extends Application {

    public void start(Stage stage) throws Exception {
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("select.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Travel Planner");
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
