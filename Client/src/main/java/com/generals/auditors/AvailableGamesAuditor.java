package com.generals.audirors;

import com.generals.MainApplication;
import com.generals.serialized_models.AvailableGameInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AvailableGamesAuditor extends Thread {
    private List<AvailableGameInfo> availableGames;
    private final IntegerProperty version = new SimpleIntegerProperty(0);
    private AtomicBoolean isEnd = new AtomicBoolean(false);
    private Object mutex = new Object();

    public AvailableGamesAuditor(List<AvailableGameInfo> availableGames) {
        this.availableGames = availableGames;
        setDaemon(true);
    }

    public void run() {
        System.out.println("Running " + this.getClass().getSimpleName() + " " + Thread.currentThread().getName());
        while (!isEnd.get()) {
            System.out.println(this.getClass().getSimpleName() + ": Getting list of available games from Server");
            String content = MainApplication.getServerConnection().readContentFromServer();
            AvailableGameInfo[] availableGamesArray;
            try {
                availableGamesArray = new Gson().fromJson(content, AvailableGameInfo[].class);
                synchronized (mutex) {
                    Object[] prevAvailableGamesArray = availableGames.toArray().clone();
                    if (!Arrays.deepEquals(availableGamesArray, prevAvailableGamesArray)) {
                        System.out.println(this.getClass().getSimpleName() +
                                ": List of games is different, request a change of view");
                        availableGames.clear();
                        availableGames.addAll(Arrays.asList(availableGamesArray));
                        version.setValue(version.getValue() + 1);
                    }
                }
            } catch (JsonSyntaxException exception) {
                isEnd.set(true);
                // end of work: info for next stage was
                // TODO: end when handling command "connected to game waiting room"
            }
        }

//        // TODO: delete following code, it is only experiment with reading several json objects from server
//        try {
//            TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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
