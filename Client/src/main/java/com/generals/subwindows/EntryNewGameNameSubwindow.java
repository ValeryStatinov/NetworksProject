package com.generals.subwindows;

import com.generals.MainApplication;
import com.generals.ServerConnection;
import com.generals.serialized_models.SelectionGameCommand;
import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class EntryNewGameNameSubwindow {
    private Label topText;
    private TextField textField;
    private Button createGameButton;

    public EntryNewGameNameSubwindow(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        VBox vBox = new VBox(15);
        vBox.setAlignment(Pos.CENTER);
        pane.setCenter(vBox);
        BorderPane.setMargin(vBox, new Insets(10, 10, 10, 10));

        initTopText();
        initTextField();
        initCreateGameButton();

        vBox.getChildren().add(topText);
        vBox.getChildren().add(textField);
        vBox.getChildren().add(createGameButton);

        Scene scene = new Scene(pane, 350, 200);
        Stage substage = new Stage();
        substage.setTitle("Choose name");
        substage.setScene(scene);

        substage.initModality(Modality.WINDOW_MODAL);
        substage.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        substage.setX(primaryStage.getX() + 200);
        substage.setY(primaryStage.getY() + 100);

        substage.show();
    }

    private void initTopText() {
        topText = new Label("Please enter a name for new game:");
        topText.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
    }

    private void initTextField() {
        textField = new TextField("Enter some name");
        textField.setPrefColumnCount(1);
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
                SelectionGameCommand command1 = new SelectionGameCommand("ready_to_start");
                MainApplication.getServerConnection().sendCommandToServer(command1);
                while (true) {
                    MainApplication.getServerConnection().readContentFromServer();
                }
                // TODO: go to the next window
            }
        });
    }

    private boolean isValidGameName(String name) {
        if (name.contains(" ")) {
            return false;
        }
        return true;
    }
}
