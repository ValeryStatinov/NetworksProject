package com.generals.windows;

import com.generals.MainApplication;
import com.generals.audirors.AvailableGamesAuditor;
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

import java.util.ArrayList;
import java.util.List;


public class GameSelectionWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;

    private Stage stage;

    private Text topText;
    private Button createNewGameButton;
    private Button connectToChosenGameButton;
    private ListView<String> gamesListView;

    private List<AvailableGameInfo> availableGames = new ArrayList<AvailableGameInfo>();
    private Integer selectedGameId = null;
    private AvailableGamesAuditor auditor;

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
                MainApplication.getServerConnection().sendCommandToServer(command);
                new GameWaitingRoomWindow(stage);
            }
        });
    }

    private void initCreateNewGameButton() {
        createNewGameButton = new Button("Create new game");
        createNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Create new game'");
                EntryNewGameNameSubwindow subWindow = new EntryNewGameNameSubwindow(stage, auditor);
            }
        });
    }

    private void initGamesListView() {
        auditor = new AvailableGamesAuditor(availableGames);
        auditor.start();
        gamesListView = new ListView<String>();
        updateGamesListView();
        gamesListView.setPrefWidth(300);
        gamesListView.setPrefHeight(200);
        gamesListView.setStyle("-fx-background-color: #0e0c32; " +
                "-fx-font-size: 18; " +
                "-fx-font-family: 'DejaVu Sans'");
        gamesListView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            public void onChanged(Change<? extends Integer> c) {
                System.out.println("onChanged ListView");
                // TODO:
//                synchronized (auditor.getMutex()) {
//                    System.out.println("Super values: " + c.getList().get(0) + " " + c.getList().size());
//                    selectedGameId = availableGames.get(c.getList().get(0)).game_id;
//                }
//                System.out.println("Selected game id = " + selectedGameId);
//                connectToChosenGameButton.setDisable(false);
            }
        });
        auditor.getVersion().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateGamesListView();
            }
        });
    }

    private void updateGamesListView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        synchronized (auditor.getMutex()) {
            for (AvailableGameInfo info : availableGames) {
                items.add(info.toString());
            }
        }
        gamesListView.setItems(items);
        System.out.println("GamesListView was updated");
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
}



