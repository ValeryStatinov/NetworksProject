package com.generals.windows;

import com.generals.MainApplication;
import com.generals.serialized_models.AvailableGameInfo;
import com.generals.serialized_models.ConnectionGameCommand;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.*;

import java.io.*;

import com.google.gson.Gson;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sun.applet.Main;


public class GameSelectionWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;
    private Stage stage;
    private AvailableGameInfo availableGamesList[];
    private Integer selectedGameId = null;

    public GameSelectionWindow(Stage stage) {
        this.stage = stage;
        setAvailableGamesListFromServer();
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        String style = "-fx-background-color: rgba(13,9,6,0.76)";
        pane.setStyle(style);

        HBox hbox = new HBox();
        pane.setTop(hbox);
        hbox.setPadding(new Insets(10, 20, 30, 40));
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().add(getText());

        VBox buttonsBox = new VBox(20);
        final Button connectToChosenGameButton = getConnectToChosenGameButton();
        buttonsBox.getChildren().add(connectToChosenGameButton);
        Button createNewGameButton = getCreateNewGameButton();
        buttonsBox.getChildren().add(createNewGameButton);
        pane.setRight(buttonsBox);
        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (AvailableGameInfo info : availableGamesList) {
            items.add(info.toString());
        }
        list.setPrefWidth(300);
        list.setPrefHeight(200);
        list.setItems(items);
        list.setStyle("-fx-background-color: black; -fx-font-size: 18; -fx-font-family: 'DejaVu Sans'");
        list.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            public void onChanged(Change<? extends Integer> c) {
                selectedGameId = availableGamesList[c.getList().get(0)].game_id;
                System.out.println("Selected game id = " + selectedGameId);
                connectToChosenGameButton.setDisable(false);
            }
        });

        pane.setLeft(list);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }

    private Text getText() {
        Text text = new Text("List of available games:");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        text.setFill(Color.WHITE);
        return text;
    }

    private void setAvailableGamesListFromServer() {
        System.out.println("Getting list of available games from Server");
        String content = MainApplication.readContentFromServer();
        AvailableGameInfo info = new Gson().fromJson(content, AvailableGameInfo.class);
        availableGamesList = new AvailableGameInfo[1];
        availableGamesList[0] = info;
//        availableGamesList = new Gson().fromJson(content, AvailableGameInfo[].class);
    }

//    private void setAvailableGamesListSomehow() {
//        System.out.println("Init list of games somehow");
//        availableGamesList = new AvailableGameInfo[5];
//        for (int i = 0; i < 5; i++) {
//            availableGamesList[i] = new AvailableGameInfo();
//        }
//        availableGamesList[1].numFreeSlots = 3;
//        availableGamesList[1].id = 13534534;
//        availableGamesList[1].numConnectedpPayers = 5;
//        availableGamesList[1].name = "newGame!!!";
//    }

    private Button getConnectToChosenGameButton() {
        Button button = new Button("Connect to game!");
        button.setDisable(true);
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Connect to game!'");
                System.out.println("Connecting to game #" + selectedGameId);
                ConnectionGameCommand command = new ConnectionGameCommand("join");
                command.setGameId(selectedGameId);
                sendCommandToServer(command);
                ConnectionGameCommand command1 = new ConnectionGameCommand("ready_to_start");
                sendCommandToServer(command1);
                while (true) {
                    MainApplication.readContentFromServer();
                }
            }
        });
        return button;
    }

    private Button getCreateNewGameButton() {
        Button button = new Button("Create new game");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Create new game'");
                ConnectionGameCommand command = new ConnectionGameCommand("create_game");
                command.setName("SOME_NAME");
                sendCommandToServer(command);
            }
        });
        return button;
    }

    private void sendCommandToServer(ConnectionGameCommand command) {
        System.out.println("Sending command to server: " + command);
        Gson gson = new Gson();
        String stringToSend = gson.toJson(command);
        MainApplication.writeContentToServer(stringToSend);
    }
}
