package com.generals.windows;

import com.generals.MainApplication;
import com.generals.auditors.GameAuditor;
import com.generals.auditors.WaitingRoomAuditor;
import com.generals.serialized_models.SelectionGameCommand;
import com.generals.serialized_models.WaitingRoomInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameWindow implements Window {
    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 600;

    private Stage stage;

    private Text mainText;

    private StringBuilder gameInfo = new StringBuilder("Game text:");
    private GameAuditor auditor;

    public GameWindow(Stage stage) {
        this.stage = stage;
        stage.setScene(getScene());
        System.out.println("Showing " + this.getClass().getSimpleName());
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        String style = "-fx-background-color: rgba(28,14,80,0.76)";
        pane.setStyle(style);

        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        pane.setCenter(vBox);
        BorderPane.setMargin(vBox, new Insets(10, 50, 10, 50));

        initMainText();
        vBox.getChildren().add(mainText);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;
    }

    private void initMainText() {
        auditor = new GameAuditor(gameInfo);
        auditor.start();

        mainText = new Text();
        mainText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        mainText.setFill(Color.LIGHTYELLOW);

        auditor.getVersion().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        synchronized (auditor.getMutex()) {
                            mainText.setText(gameInfo.toString());
                            System.out.println("Changed text");
                        }
                    }
                });
            }
        });
    }
}
