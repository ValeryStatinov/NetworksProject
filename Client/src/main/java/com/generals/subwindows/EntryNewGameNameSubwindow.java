package com.generals.subwindows;

import com.generals.MainApplication;
import com.generals.audirors.AvailableGamesAuditor;
import com.generals.serialized_models.SelectionGameCommand;
import com.generals.windows.GameWaitingRoomWindow;
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
import javafx.scene.text.*;
import javafx.stage.*;

public class EntryNewGameNameSubwindow implements Window {
    private static int WINDOW_WIDTH = 350;
    private static int WINDOW_HEIGHT = 200;

    private Stage primaryStage;
    private Stage substage;
    private AvailableGamesAuditor auditor;

    private Label topText;
    private TextField textField;
    private Button createGameButton;

    public EntryNewGameNameSubwindow(Stage primaryStage, AvailableGamesAuditor auditor) {
        this.auditor = auditor;
        this.primaryStage = primaryStage;
        substage = new Stage();
        substage.setTitle("Choose name");
        substage.setScene(getScene());

        substage.initOwner(primaryStage);
        substage.initModality(Modality.WINDOW_MODAL);

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
                System.out.println("Closing thread: " + Thread.currentThread().getName());
                substage.close();
                System.out.println("HERE");
                SelectionGameCommand command = new SelectionGameCommand("create_game");
                command.setName(textField.getText());
                MainApplication.getServerConnection().sendCommandToServer(command);
                new GameWaitingRoomWindow(primaryStage);
                // TODO: go to the next window
            }
        });
    }

    public Scene getScene() {
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

    public void close() {
        substage.close();
    }

    private boolean isValidGameName(String name) {
        if (name.contains(" ")) {
            return false;
        }
        return true;
    }
}
