package com.generals.auditors;

import com.generals.MainApplication;
import com.generals.serialized_models.WaitingRoomInfo;
import com.generals.windows.WaitingRoomWindow;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.concurrent.atomic.AtomicBoolean;

public class WaitingRoomAuditor extends Thread {
    private WaitingRoomWindow window;
    private WaitingRoomInfo waitingRoomInfo;
    private final IntegerProperty version = new SimpleIntegerProperty(0);
    private AtomicBoolean isEnd = new AtomicBoolean(false);
    private Object mutex = new Object();

    public WaitingRoomAuditor(WaitingRoomInfo waitingRoomInfo, WaitingRoomWindow window) {
        this.waitingRoomInfo = waitingRoomInfo;
        this.window = window;
        setDaemon(true);
    }

    public void run() {
        System.out.println("Running " + this.getClass().getSimpleName() + " " + Thread.currentThread().getName());
        while (!isEnd.get()) {
            System.out.println(this.getClass().getSimpleName() + ": Getting waiting room status from Server");
            String content = MainApplication.getServerConnection().readContentFromServer();

            // TODO: go to game window
            if (content.equals("{\"hello\": \"world\"}")) {
                stopWork();
                window.startGame();
                break;
            }

            WaitingRoomInfo newWaitingRoomInfo;
            try {
                newWaitingRoomInfo = new Gson().fromJson(content, WaitingRoomInfo.class);
                synchronized (mutex) {
                    if (!newWaitingRoomInfo.equals(waitingRoomInfo)) {
                        System.out.println(this.getClass().getSimpleName() +
                                ": waiting room status is different, request a change of view");
                        waitingRoomInfo.set(newWaitingRoomInfo);
                        version.setValue(version.getValue() + 1);
                    }
                }
            } catch (JsonSyntaxException exception) {
                System.out.println("JsonSyntaxException in " + Thread.currentThread().getName());
                stopWork();
                // end of work: info for next stage was
                // TODO: end when handling command "connected to game"
            }
        }
        System.out.println("Finishing " + this.getClass().getSimpleName() + " " + Thread.currentThread().getName());
    }

    public IntegerProperty getVersion() {
        return version;
    }

    public Object getMutex() {
        return mutex;
    }

    public void stopWork() {
        isEnd.set(true);
    }
}

