package com.example.dsa2_ca2.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        // load the fxml
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/dsa2_ca2/view.fxml"));

        // put the loaded fxml into a scene
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // stylesheet

        // window setup
        stage.setTitle("National Gallery Route Finder");
        stage.setScene(scene);

        stage.show();
    }
}
