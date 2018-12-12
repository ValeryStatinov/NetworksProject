package com.generals.auditors;

import com.generals.MainApplication;
import com.google.gson.JsonSyntaxException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameAuditor extends Thread {
    private StringBuilder gameInfo;
    private final IntegerProperty version = new SimpleIntegerProperty(0);
    private AtomicBoolean isEnd = new AtomicBoolean(false);
    private Object mutex = new Object();

    public GameAuditor(StringBuilder gameInfo) {
        this.gameInfo = gameInfo;
        setDaemon(true);
    }

    public void run() {
        System.out.println("Running " + this.getClass().getSimpleName() + " " + Thread.currentThread().getName());
        while (!isEnd.get()) {
            System.out.println(this.getClass().getSimpleName() + ": Getting game info from Server");
            String content = MainApplication.getServerConnection().readContentFromServer();
            try {
                synchronized (mutex) {
                    gameInfo.append("\n" + content);
                    version.setValue(version.getValue() + 1);
                }
            } catch (JsonSyntaxException exception) {
                System.out.println("JsonSyntaxException in " + Thread.currentThread().getName());
                stopWork();
                // end of work: info for next stage was
                // TODO: handle game commands
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
