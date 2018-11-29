package com.generals;

import com.generals.models.AvailableGameInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.gson.Gson;


public class GameWindow {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8888;
    private Stage stage;
    private Socket socket;
    private AvailableGameInfo availableGamesList[];

    public GameWindow(Stage stage) {
        this.stage = stage;
        try {
//            connectToServer();
//            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String content = br.readLine();
//            System.out.println("Get content from server: " + content);
//            gameInfo[] = new Gson().fromJson(content, AvailableGameInfo[].class);
            setAvailableGamesListSomehow();

            Scene scene = getScene();
            stage.setScene(scene);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private void connectToServer() throws UnknownHostException, IOException {
        System.out.println("Connecting to server...");
        InetAddress ipAddress = InetAddress.getByName(SERVER_ADDRESS);
        socket = new Socket(ipAddress, SERVER_PORT);
        System.out.println("Connected to server!");
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();

        HBox hbox = new HBox();
        pane.setTop(hbox);
        String style = "-fx-background-color: rgba(13,9,6,0.76)";
        hbox.setStyle(style);
        hbox.setPadding(new Insets(10, 20, 30, 40));
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().add(getText());

        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (AvailableGameInfo info : availableGamesList) {
            items.add(info.toString());
        }
        list.setPrefWidth(200);
        list.setPrefHeight(200);
        list.setItems(items);
        list.setStyle("-fx-background-color: black; -fx-font-size: 18; -fx-font-family: 'DejaVu Sans'");
        pane.setLeft(list);

//        Button serverConnectionButton = getServerConnectionButton();
//        vbox.getChildren().add(welcomeText);
//        vbox.getChildren().add(serverConnectionButton);
        Scene menuScene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return menuScene;
    }

    private Text getText() {
        Text text = new Text("List of available games:");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        text.setFill(Color.WHITE);
        return text;
    }

    private void setAvailableGamesListSomehow() {
        System.out.println("Init list of games somehow");
        availableGamesList = new AvailableGameInfo[5];
        for (int i = 0; i < 5; i++) {
            availableGamesList[i] = new AvailableGameInfo();
        }
        availableGamesList[1].numFreeSlots = 3;
        availableGamesList[1].id = 13534534;
        availableGamesList[1].numConnectedplayers = 5;
        availableGamesList[1].name = "newGame!!!";
    }
}
