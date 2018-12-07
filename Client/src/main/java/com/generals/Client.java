package com.generals;

import java.net.*;
import java.io.*;


public class Client {
    private final String serverAddress = "127.0.0.1";
    private final int serverPort = 8888;

    public static void main(String[] args) {
        Client client = new Client();
        try {
            System.out.println("Connecting to server...");
            InetAddress ipAddress = InetAddress.getByName(client.serverAddress);
            Socket socket = new Socket(ipAddress, client.serverPort);
            System.out.println("Connected!");
            InputStream in = socket.getInputStream();
            byte buf[] = new byte[64*1024];
            String data = null;
            while (true) {
                int r = in.read(buf);
                data = new String(buf, 0, r);
                System.out.println("Get massage from server: " + data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
