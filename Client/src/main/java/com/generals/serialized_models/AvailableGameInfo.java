package com.generals.serialized_models;

public class AvailableGameInfo {
    public int game_id;
    public String name;
    public int empty_slots;
    public int max_slots;

    @Override
    public String toString() {
        return new String("#" + game_id + " " + name + " " + (max_slots - empty_slots) + "/" + max_slots);
    }
}
