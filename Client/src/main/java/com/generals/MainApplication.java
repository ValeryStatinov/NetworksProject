package com.generals;

import javafx.application.Application;
import javafx.stage.Stage;
import com.generals.windows.*;

import java.io.IOException;

public class MainApplication extends Application {
    private static ServerConnection serverConnection = null;

    @Override
    public void start(Stage primaryStage) {
        try {
            Stage stage = primaryStage;
            stage.setTitle("Generals");
            WelcomeWindow window = new WelcomeWindow(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static boolean initServerConnection(String serverAddress, int serverPort) {
        if (serverConnection == null) {
            try {
                serverConnection = new ServerConnection(serverAddress, serverPort);
                return true;
            } catch (IOException exception) {
                return false;
            }
        }
        return false;
    }

    public static ServerConnection getServerConnection() {
        return serverConnection;
    }
}
