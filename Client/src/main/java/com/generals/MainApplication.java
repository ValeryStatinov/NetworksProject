package com.generals;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MainMenu menu = new MainMenu(primaryStage);
            Stage stage = menu.getStage();
            System.out.println("Showing menu");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
