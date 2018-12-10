package com.generals.windows;

import com.generals.MainApplication;
import com.generals.serialized_models.AvailableGameInfo;
import com.generals.serialized_models.SelectionGameCommand;
import com.generals.subwindows.EntryNewGameNameSubwindow;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;

import com.google.gson.Gson;


public class GameSelectionWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;

    private Stage stage;

    private Text topText;
    private Button createNewGameButton;
    private Button connectToChosenGameButton;
    private ListView<String> gamesListView;

    private AvailableGameInfo availableGames[];
    private Integer selectedGameId = null;

    public GameSelectionWindow(Stage stage) {
        this.stage = stage;
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    private void initTopText() {
        topText = new Text("List of available games:");
        topText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        topText.setFill(Color.LIGHTYELLOW);
    }

    private void initConnectToChosenGameButton() {
        connectToChosenGameButton = new Button("Connect to game!");
        connectToChosenGameButton.setDisable(true);
        connectToChosenGameButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Connect to game!'");
                System.out.println("Connecting to game #" + selectedGameId);
                SelectionGameCommand command = new SelectionGameCommand("join");
                command.setGameId(selectedGameId);
                sendCommandToServer(command);
                SelectionGameCommand command1 = new SelectionGameCommand("ready_to_start");
                sendCommandToServer(command1);
                while (true) {
                    MainApplication.readContentFromServer();
                }
            }
        });
    }

    private void initCreateNewGameButton() {
        createNewGameButton = new Button("Create new game");
        createNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Create new game'");
                EntryNewGameNameSubwindow subWindow = new EntryNewGameNameSubwindow(stage);
            }
        });
    }

    private void initGamesListView() {
        setAvailableGamesFromServer();
        gamesListView = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (AvailableGameInfo info : availableGames) {
            items.add(info.toString());
        }
        gamesListView.setItems(items);
        gamesListView.setPrefWidth(300);
        gamesListView.setPrefHeight(200);
        gamesListView.setStyle("-fx-background-color: #0e0c32; " +
                "-fx-font-size: 18; " +
                "-fx-font-family: 'DejaVu Sans'");
        gamesListView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            public void onChanged(Change<? extends Integer> c) {
                selectedGameId = availableGames[c.getList().get(0)].game_id;
                System.out.println("Selected game id = " + selectedGameId);
                connectToChosenGameButton.setDisable(false);
            }
        });
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        String style = "-fx-background-color: rgba(28,14,80,0.76)";
        pane.setStyle(style);

        // Up section (with text)
        initTopText();
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().add(topText);
        hbox.setPadding(new Insets(10, 20, 20, 10));
        pane.setTop(hbox);

        // Right section (with buttons)
        initConnectToChosenGameButton();
        initCreateNewGameButton();
        VBox buttonsBox = new VBox(40);
        buttonsBox.getChildren().add(connectToChosenGameButton);
        buttonsBox.getChildren().add(createNewGameButton);
        buttonsBox.setPadding(new Insets(10, 30, 10, 10));
        pane.setRight(buttonsBox);


        // Left section (with list)
        initGamesListView();
        pane.setLeft(gamesListView);
        BorderPane.setMargin(gamesListView, new Insets(0, 10, 15, 20));

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }

    private void setAvailableGamesFromServer() {
        System.out.println("Getting list of available games from Server");
        String content = MainApplication.readContentFromServer();
        availableGames = new Gson().fromJson(content, AvailableGameInfo[].class);
    }

    private void sendCommandToServer(SelectionGameCommand command) {
        System.out.println("Sending command to server: " + command);
        Gson gson = new Gson();
        String stringToSend = gson.toJson(command);
        MainApplication.writeContentToServer(stringToSend);
    }
}



