package com.generals.windows;

import com.generals.MainApplication;
import com.generals.serialized_models.SelectionGameCommand;
import com.generals.subwindows.EntryNewGameNameSubwindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class GameWaitingRoomWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;

    private Stage stage;

    private Text topText;
    private Label playersStatusLabel;
    private Button readyToPlayButton;

    public GameWaitingRoomWindow(Stage stage) {
        this.stage = stage;
        // TODO: parallel handling: reading data from server about current number of players
        // and refreshing window
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
        while (true) {
            MainApplication.getServerConnection().readContentFromServer();
        }
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        String style = "-fx-background-color: rgba(28,14,80,0.76)";
        pane.setStyle(style);

        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        pane.setCenter(vBox);
        BorderPane.setMargin(vBox, new Insets(10, 10, 10, 10));

        // Up section (with text)
        initTopText();
        vBox.getChildren().add(topText);

        // Center section (with label)
        initPlayersStatusLabel();
        vBox.getChildren().add(playersStatusLabel);


//        vBox.getChildren().add(createGameButton);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }

    private void initTopText() {
        topText = new Text("Waiting for all players...");
        topText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        topText.setFill(Color.LIGHTYELLOW);
    }

    private void initPlayersStatusLabel() {
        playersStatusLabel = new Label("players status label");
        playersStatusLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
    }

    private void initReadyToPlayButton() {
        readyToPlayButton = new Button("Ready to play");
        readyToPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Ready to play'");
                SelectionGameCommand command = new SelectionGameCommand("ready_to_start");
                MainApplication.getServerConnection().sendCommandToServer(command);
                while (true) {
                    MainApplication.getServerConnection().readContentFromServer();
                }
            }
        });
    }
}
