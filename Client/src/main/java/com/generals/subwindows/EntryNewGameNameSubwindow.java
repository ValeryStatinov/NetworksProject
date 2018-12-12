package com.generals.subwindows;

import com.generals.MainApplication;
import com.generals.serialized_models.SelectionGameCommand;
import com.generals.windows.WaitingRoomWindow;
import com.generals.windows.Window;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;

public class EntryNewGameNameSubwindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;

    private Stage stage;
    private Scene prevScene;

    private Text topText;
    private TextField textField;
    private Button createGameButton;
    private Button returnToGamesListButton;

    public EntryNewGameNameSubwindow(Stage stage, Scene prevScene) {
        this.stage = stage;
        this.prevScene = prevScene;
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    private void initTopText() {
        topText = new Text("Please enter a name for new game:");
        topText.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
        topText.setFill(Color.LIGHTYELLOW);
    }

    private void initTextField() {
        textField = new TextField("Enter some name");
        textField.setPrefColumnCount(1);
        textField.prefWidth(40);
        textField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (isValidGameName(oldValue) && !isValidGameName(newValue)) {
                    createGameButton.setDisable(true);
                } else if (isValidGameName(newValue) && !isValidGameName(oldValue)) {
                    createGameButton.setDisable(false);
                }
            }
        });
    }

    private void initCreateGameButton() {
        createGameButton = new Button("Create game");
        createGameButton.setDisable(true);
        createGameButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                SelectionGameCommand command = new SelectionGameCommand("create_game");
                command.setName(textField.getText());
                MainApplication.getServerConnection().sendCommandToServer(command);
                new WaitingRoomWindow(stage);
            }
        });
    }

    private void initReturnToGamesListButton() {
        returnToGamesListButton = new Button("Return to games list");
        returnToGamesListButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Pressed 'Return to games list' button");
                stage.setScene(prevScene);
            }
        });
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        String style = "-fx-background-color: rgba(28,14,80,0.76)";
        pane.setStyle(style);

        VBox vBox = new VBox(30);
        vBox.setAlignment(Pos.CENTER);
        pane.setCenter(vBox);
        BorderPane.setMargin(vBox, new Insets(10, 50, 10, 50));

        initTopText();
        initTextField();
        initCreateGameButton();

        vBox.getChildren().add(topText);
        vBox.getChildren().add(textField);
        vBox.getChildren().add(createGameButton);


        HBox hBox = new HBox(15);
        pane.setBottom(hBox);
        BorderPane.setMargin(hBox, new Insets(10, 10, 10, 10));
        initReturnToGamesListButton();
        hBox.getChildren().add(returnToGamesListButton);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER && !createGameButton.isDisable()) {
                    createGameButton.fire();
                    event.consume();
                }
            }
        });

        return scene;
    }

    private boolean isValidGameName(String name) {
        if (name.contains(" ")) {
            return false;
        }
        return true;
    }
}
