package com.generals.windows;

import com.generals.MainApplication;
import com.generals.auditors.WaitingRoomAuditor;
import com.generals.serialized_models.SelectionGameCommand;
import com.generals.serialized_models.WaitingRoomInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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


public class WaitingRoomWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;

    private Stage stage;

    private Text topText;
    private Label playersStatusLabel;
    private Button readyToPlayButton;
    private Button returnToGamesListButton;

    private WaitingRoomInfo waitingRoomInfo = new WaitingRoomInfo();
    private WaitingRoomAuditor auditor;

    public WaitingRoomWindow(Stage stage) {
        this.stage = stage;
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        String style = "-fx-background-color: rgba(28,14,80,0.76)";
        pane.setStyle(style);

        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        pane.setCenter(vBox);
        BorderPane.setMargin(vBox, new Insets(10, 50, 10, 50));

        initTopText();
        vBox.getChildren().add(topText);
        initPlayersStatusLabel();
        vBox.getChildren().add(playersStatusLabel);
        initReadyToPlayButton();
        vBox.getChildren().add(readyToPlayButton);

        HBox hBox = new HBox(15);
        pane.setBottom(hBox);
        BorderPane.setMargin(hBox, new Insets(10, 10, 10, 10));
        initReturnToGamesListButton();
        hBox.getChildren().add(returnToGamesListButton);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }

    private void initTopText() {
        topText = new Text("Waiting for all players...");
        topText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        topText.setFill(Color.LIGHTYELLOW);
    }

    private void initPlayersStatusLabel() {
        auditor = new WaitingRoomAuditor(waitingRoomInfo);
        auditor.start();

        playersStatusLabel = new Label("players status label");
        playersStatusLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));

        auditor.getVersion().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        synchronized (auditor.getMutex()) {
                            playersStatusLabel.setText(waitingRoomInfo.toString());
                            System.out.println("Waiting room status label was updated!");
                        }
                    }
                });
            }
        });
    }

    private void initReadyToPlayButton() {
        readyToPlayButton = new Button("Ready to play");
        readyToPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                readyToPlayButton.setDisable(true);
                System.out.println("Pressed button 'Ready to play'");
                SelectionGameCommand command = new SelectionGameCommand("ready_to_start");
                MainApplication.getServerConnection().sendCommandToServer(command);
//                while (true) {
//                    MainApplication.getServerConnection().readContentFromServer();
//                }
            }
        });
    }

    private void initReturnToGamesListButton() {
        returnToGamesListButton = new Button("Return to games list");
        returnToGamesListButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed 'Return to games list' button");
                SelectionGameCommand command = new SelectionGameCommand("leave_game");
                new GameSelectionWindow(stage);
            }
        });
    }
}
