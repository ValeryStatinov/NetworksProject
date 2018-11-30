package com.generals;

import com.generals.models.AvailableGameInfo;
import com.generals.models.ConnectionGameCommand;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

import java.io.*;
import java.net.*;

import com.google.gson.Gson;


public class GameWindow {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8888;
    private Stage stage;
    private Socket socket;
    private AvailableGameInfo availableGamesList[];
    private Integer selectedGameId = null;

    public GameWindow(Stage stage) {
        this.stage = stage;
        try {
            connectToServer();
            if (socket != null) {
                setAvailableGamesListFromSocket();
            } else {
                setAvailableGamesListSomehow();
            }
            setScene();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private void connectToServer() throws IOException {
        System.out.println("Connecting to server...");
        InetAddress ipAddress = InetAddress.getByName(SERVER_ADDRESS);
        socket = new Socket(ipAddress, SERVER_PORT);
        System.out.println("Connected to server!");
    }

    public void setScene() {
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
                selectedGameId = availableGamesList[c.getList().get(0)].id;
                System.out.println("Selected game id = " + selectedGameId);
                connectToChosenGameButton.setDisable(false);
            }
        });

        pane.setLeft(list);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    private Text getText() {
        Text text = new Text("List of available games:");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        text.setFill(Color.WHITE);
        return text;
    }

    private void setAvailableGamesListFromSocket() throws IOException {
        System.out.println("Getting list of available games from Server");
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String content = br.readLine();
        System.out.println("Get content from server: " + content);
        availableGamesList = new Gson().fromJson(content, AvailableGameInfo[].class);
    }

    private void setAvailableGamesListSomehow() {
        System.out.println("Init list of games somehow");
        availableGamesList = new AvailableGameInfo[5];
        for (int i = 0; i < 5; i++) {
            availableGamesList[i] = new AvailableGameInfo();
        }
        availableGamesList[1].numFreeSlots = 3;
        availableGamesList[1].id = 13534534;
        availableGamesList[1].numConnectedpPayers = 5;
        availableGamesList[1].name = "newGame!!!";
    }

    private Button getConnectToChosenGameButton() {
        Button button = new Button("Connect to game!");
        button.setDisable(true);
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Connect to game!'");
                System.out.println("Connecting to game #" + selectedGameId);
            }
        });
        return button;
    }

    private Button getCreateNewGameButton() {
        Button button = new Button("Create new game");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Create new game'");
                ConnectionGameCommand command = new ConnectionGameCommand("create_game", "Game#5");
                sendCommandToServer(command);
            }
        });
        return button;
    }

    private void sendCommandToServer(ConnectionGameCommand command) {
        System.out.println("Sending command to server: " + command);
        Gson gson = new Gson();
        String stringToSend = gson.toJson(command);
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(stringToSend);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
