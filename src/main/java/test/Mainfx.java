package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Mainfx extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // load the fxml file
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/addLine.fxml"));
     //   FXMLLoader loader = new FXMLLoader(getClass().getResource("/showProduct.fxml"));
        // load fxml code in a scene
        Parent root= loader.load();
        // put the fxml file in a scene
        Scene scene = new Scene(root);
        // set a scene in stage
        stage.setScene(scene);
        stage.setTitle("Line Management");
        stage.show();




    }

    public static void main(String[] args) {
        launch(args);
    }
}
