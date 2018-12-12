package com.generals.auditors;

import com.generals.MainApplication;
import com.generals.serialized_models.AvailableGameInfo;
import com.generals.serialized_models.WaitingRoomInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class WaitingRoomAuditor extends Thread {
    private WaitingRoomInfo waitingRoomInfo;
    private final IntegerProperty version = new SimpleIntegerProperty(0);
    private AtomicBoolean isEnd = new AtomicBoolean(false);
    private Object mutex = new Object();

    public WaitingRoomAuditor(WaitingRoomInfo waitingRoomInfo) {
        this.waitingRoomInfo = waitingRoomInfo;
        setDaemon(true);
    }

    public void run() {
        System.out.println("Running " + this.getClass().getSimpleName() + " " + Thread.currentThread().getName());
        while (!isEnd.get()) {
            System.out.println(this.getClass().getSimpleName() + ": Getting waiting room status from Server");
            String content = MainApplication.getServerConnection().readContentFromServer();
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

