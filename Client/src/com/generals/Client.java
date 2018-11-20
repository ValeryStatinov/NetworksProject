package com.generals;

import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) {
        int serverPort = 8888;
        String address = "127.0.0.1";

        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            Socket socket = new Socket(ipAddress, serverPort);
            System.out.println("Connected to server");
            InputStream in = socket.getInputStream();

            byte buf[] = new byte[64*1024];
            String data = null;

            while (true) {
                int r = in.read(buf);
                data = new String(buf, 0, r);
                System.out.println("Get massage from server: " + data);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
