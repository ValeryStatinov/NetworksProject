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

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!(other instanceof AvailableGameInfo)) {
            return false;
        }
        AvailableGameInfo otherInfo = (AvailableGameInfo) other;
        return game_id == otherInfo.game_id && name.equals(otherInfo.name) &&
                empty_slots == otherInfo.empty_slots && max_slots == otherInfo.max_slots;
    }

    @Override
    public int hashCode() {
        return game_id & name.hashCode() & empty_slots & max_slots;
    }
}
