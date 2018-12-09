package com.generals.windows;

import com.generals.MainApplication;
import com.generals.serialized_models.AvailableGameInfo;
import com.generals.serialized_models.SelectionGameCommand;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.*;

import com.google.gson.Gson;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


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
        String style = "-fx-background-color: rgba(28,14,80,0.76)";
        pane.setStyle(style);

        // Up section (with text)
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 20, 20, 10));
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().add(getTopText());
        pane.setTop(hbox);

        // Right section (with buttons)
        VBox buttonsBox = new VBox(40);
        final Button connectToChosenGameButton = getConnectToChosenGameButton();
        buttonsBox.getChildren().add(connectToChosenGameButton);
        Button createNewGameButton = getCreateNewGameButton();
        buttonsBox.getChildren().add(createNewGameButton);
        buttonsBox.setPadding(new Insets(10, 30, 10, 10));
        pane.setRight(buttonsBox);


        // Left section (with list)
        VBox listBox = new VBox(20);
        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (AvailableGameInfo info : availableGamesList) {
            items.add(info.toString());
        }
        list.setPrefWidth(300);
        list.setPrefHeight(200);
        list.setItems(items);
        list.setStyle("-fx-background-color: #0e0c32; -fx-font-size: 18; -fx-font-family: 'DejaVu Sans'");
        list.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            public void onChanged(Change<? extends Integer> c) {
                selectedGameId = availableGamesList[c.getList().get(0)].game_id;
                System.out.println("Selected game id = " + selectedGameId);
                connectToChosenGameButton.setDisable(false);
            }
        });
        pane.setLeft(list);
        BorderPane.setMargin(list, new Insets(0, 10, 15, 20));

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }

    private Text getTopText() {
        Text text = new Text("List of available games:");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        text.setFill(Color.LIGHTYELLOW);
        return text;
    }

    private void setAvailableGamesListFromServer() {
        System.out.println("Getting list of available games from Server");
        String content = MainApplication.readContentFromServer();
        availableGamesList = new Gson().fromJson(content, AvailableGameInfo[].class);
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
        return button;
    }

    private Button getCreateNewGameButton() {
        Button button = new Button("Create new game");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Create new game'");
                EnteringNewGameNameWindow subWindow = new EnteringNewGameNameWindow(stage);
            }
        });
        return button;
    }

    private void sendCommandToServer(SelectionGameCommand command) {
        System.out.println("Sending command to server: " + command);
        Gson gson = new Gson();
        String stringToSend = gson.toJson(command);
        MainApplication.writeContentToServer(stringToSend);
    }

    public class EnteringNewGameNameWindow {
        public EnteringNewGameNameWindow(Stage primaryStage) {
            BorderPane pane = new BorderPane();
            VBox vBox = new VBox(15);
            vBox.setAlignment(Pos.CENTER);
            pane.setCenter(vBox);
            BorderPane.setMargin(vBox, new Insets(10, 10, 10, 10));

            Label label = new Label("Please enter a name for new game:");
            label.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));

            final Button button = new Button("Create game");
            button.setDisable(true);

            final TextField textField = new TextField("Enter some name");
            textField.setPrefColumnCount(1);

            button.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    SelectionGameCommand command = new SelectionGameCommand("create_game");
                    command.setName(textField.getText());
                    sendCommandToServer(command);
                    SelectionGameCommand command1 = new SelectionGameCommand("ready_to_start");
                    sendCommandToServer(command1);
                    while (true) {
                        MainApplication.readContentFromServer();
                    }
                }
            });

            textField.textProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (isValidGameName(oldValue) && !isValidGameName(newValue)) {
                        button.setDisable(true);
                    } else if (isValidGameName(newValue) && !isValidGameName(oldValue)) {
                        button.setDisable(false);
                    }
                }
            });

            vBox.getChildren().add(label);
            vBox.getChildren().add(textField);
            vBox.getChildren().add(button);

            Scene secondScene = new Scene(pane, 350, 200);

            Stage newWindow = new Stage();
            newWindow.setTitle("Choose name");
            newWindow.setScene(secondScene);

            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(primaryStage);

            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);

            newWindow.show();
        }

        private boolean isValidGameName(String name) {
            if (name.contains(" ")) {
                return false;
            }
            return true;
        }
    }
}



