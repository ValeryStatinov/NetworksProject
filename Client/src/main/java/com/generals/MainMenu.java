package com.generals;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainMenu {
    private static int WINDOW_WIDTH = 500;
    private static int WINDOW_HEIGHT = 300;
    private Stage stage;

    public MainMenu(Stage primaryStage) {
        stage = primaryStage;
        Scene scene = getMenuScene();
        stage.setScene(scene);
    }

    private Scene getMenuScene() {
        BorderPane pane = new BorderPane();
        VBox vbox = new VBox();
        pane.setCenter(vbox);
        vbox.setStyle("-fx-background-color: rgba(13,9,6,0.76)");
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(100);
        Text welcomeText = getWelcomeText();
        Button serverConnectionButton = getServerConnectionButton();
        vbox.getChildren().add(welcomeText);
        vbox.getChildren().add(serverConnectionButton);
        Scene menuScene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return menuScene;
    }

    private Text getWelcomeText() {
        Text text = new Text("Welcome to generals game!");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        text.setFill(Color.WHITE);
        return text;
    }

    private Button getServerConnectionButton() {
        Button button = new Button("Connect to server");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'connect to server'");
                GameWindow gamesListWindow = new GameWindow(stage);
//                stage.setScene(gamesListWindow.getScene());
            }
        });
        return button;
    }

    public Stage getStage() {
        return stage;
    }
}
