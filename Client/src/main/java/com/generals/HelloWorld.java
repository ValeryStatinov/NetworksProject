package com.generals.serialized_models;

import com.google.gson.Gson;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("HelloWorld");
        Gson gson = new Gson();
//        int ints[] = new int[5];
        AvailableGameInfo games[] = new AvailableGameInfo[3];
        for (int i = 0; i < 3; i++) {
            games[i] = new AvailableGameInfo();
        }

        System.out.println(gson.toJson(games));
    }
}
