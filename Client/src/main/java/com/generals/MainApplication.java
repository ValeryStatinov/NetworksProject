package com.generals;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.generals.windows.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Stage stage = primaryStage;
            MainMenuWindow menu = new MainMenuWindow(stage);
            System.out.println("Showing menu");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8888;
    private static Socket socket = null;

    private static Socket getSocket() {
        if (socket == null) {
            try {
                System.out.println("Connecting to server...");
                InetAddress ipAddress = InetAddress.getByName(SERVER_ADDRESS);
                socket = new Socket(ipAddress, SERVER_PORT);
                System.out.println("Connected to server!");
            } catch (IOException exception) {
                System.out.println("Failed to connect to server");
                exception.printStackTrace();
            }
        }
        return socket;
    }

    public static InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = getSocket().getInputStream();
        } catch (IOException exception) {
            System.out.println("Failed to get input stream");
            exception.printStackTrace();
        }
        return inputStream;
    }

    public static OutputStream getOutputStream() {
        OutputStream outputStream = null;
        try {
            outputStream = getSocket().getOutputStream();
        } catch (IOException exception) {
            System.out.println("Failed to get output stream");
            exception.printStackTrace();
        }
        return outputStream;
    }

    public static String readContent() {
        String content = null;
        byte buf[] = new byte[64 * 1024];
        try {
            InputStream in = getInputStream();
            int r = in.read(buf);
            content = new String(buf, 0, r);
            System.out.println("Get content from server: " + content);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return content;
    }

}
