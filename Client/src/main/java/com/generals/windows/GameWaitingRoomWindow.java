package com.generals.windows;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameWaitingRoomWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;
    private Stage stage;

    public GameWaitingRoomWindow(Stage stage) {
        this.stage = stage;
//        setAvailableGamesListFromServer();
        // TODO: parallel handling: reading data from server about current number of players
        // and refreshing window
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    public Scene getScene() {
        return null;
    }
}
