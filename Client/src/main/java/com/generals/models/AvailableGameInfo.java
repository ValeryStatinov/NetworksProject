package com.generals.models;

public class AvailableGameInfo {
    public int id;
    public String name;
    public int numConnectedpPayers;
    public int numFreeSlots;

    @Override
    public String toString() {
        return new String("#" + id + " " + name + " " +
                numConnectedpPayers + "/" + (numFreeSlots + numConnectedpPayers));
    }
}
