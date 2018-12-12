package com.generals;

import com.generals.serialized_models.SelectionGameCommand;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {
    private String serverAddress;
    private int serverPort;
    private Socket socket = null;

    ServerConnection(String serverAddress, int serverPort) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        getSocket();
    }

    private Socket getSocket() throws IOException {
        if (socket == null) {
            try {
                System.out.println("Connecting to server...");
                InetAddress ipAddress = InetAddress.getByName(serverAddress);
                socket = new Socket(ipAddress, serverPort);
                System.out.println("Connection established");
            } catch (IOException exception) {
                System.out.println("Failed to connect to server");
                throw exception;
            }
        }
        return socket;
    }

    public InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = getSocket().getInputStream();
        } catch (IOException exception) {
            System.out.println("Failed to get input stream");
            exception.printStackTrace();
        }
        return inputStream;
    }

    public OutputStream getOutputStream() {
        OutputStream outputStream = null;
        try {
            outputStream = getSocket().getOutputStream();
        } catch (IOException exception) {
            System.out.println("Failed to get output stream");
            exception.printStackTrace();
        }
        return outputStream;
    }

    public String readContentFromServer() {
        String content = null;
        byte buf[] = new byte[64 * 1024];
        try {
            InputStream in = getInputStream();
            int r = in.read(buf);
            content = new String(buf, 0, r);
            System.out.println(Thread.currentThread().getName() +
                    ": Get content from server: " + content);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return content;
    }

    public void writeContentToServer(String content) {
        System.out.println(Thread.currentThread().getName() +
                ": Sending content to server: " + content);
        try {
            OutputStream outputStream = getOutputStream();
            outputStream.write(content.getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendCommandToServer(SelectionGameCommand command) {
        Gson gson = new Gson();
        String stringToSend = gson.toJson(command);
        writeContentToServer(stringToSend);
    }
}
