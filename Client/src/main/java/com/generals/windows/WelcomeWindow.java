package com.generals.windows;

import com.generals.MainApplication;
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

    private Text welcomeText;
    private Button serverConnectionButton;


    public WelcomeWindow(Stage stage) {
        this.stage = stage;
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    private void initWelcomeText() {
        welcomeText = new Text("Welcome to generals game!");
        welcomeText.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        welcomeText.setFill(Color.LIGHTYELLOW);
    }

    private void initServerConnectionButton() {
        serverConnectionButton = new Button("Connect to server");
        serverConnectionButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed button 'Connect to server'");
                MainApplication.initServerConnection("127.0.0.1", 8888);
                // TODO: subwindow with server parameters
                GameSelectionWindow gameSelectionWindow = new GameSelectionWindow(stage);
            }
        });
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: rgba(28,14,80,0.76)");
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(100);
        pane.setCenter(vbox);

        initWelcomeText();
        initServerConnectionButton();
        vbox.getChildren().add(welcomeText);
        vbox.getChildren().add(serverConnectionButton);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }
}
