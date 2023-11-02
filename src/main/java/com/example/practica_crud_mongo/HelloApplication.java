package com.example.practica_crud_mongo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Practica CRUD MongoDB (Por Raúl Sastre)");
        stage.setScene(scene);
        stage.setResizable(false);      // IMPIDE QUE SE PUEDA MODIFICAR LA RESOLUCION DE LA PANTALLA
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}