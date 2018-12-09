package com.generals.windows;

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

public class WelcomeWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 400;
    private Stage stage;

    public WelcomeWindow(Stage stage) {
        this.stage = stage;
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    private Text getWelcomeText() {
        Text text = new Text("Welcome to generals game!");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        text.setFill(Color.LIGHTYELLOW);
        return text;
    }

    private Button getServerConnectionButton() {
        Button button = new Button("Connect to server");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Connect to server'");
                GameSelectionWindow gameSelectionWindow = new GameSelectionWindow(stage);
            }
        });
        return button;
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        VBox vbox = new VBox();
        pane.setCenter(vbox);
        vbox.setStyle("-fx-background-color: rgba(28,14,80,0.76)");
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(100);
        Text welcomeText = getWelcomeText();
        Button serverConnectionButton = getServerConnectionButton();
        vbox.getChildren().add(welcomeText);
        vbox.getChildren().add(serverConnectionButton);
        Scene menuScene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return menuScene;
    }
}
