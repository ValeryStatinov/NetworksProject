package com.generals.serialized_models;

public class WaitingRoomInfo {
    public int empty_slots;
    public int max_slots;
    public int ready;

    @Override
    public String toString() {
        return new String("Fullness: " + (max_slots - empty_slots) + "/" + max_slots + "  Ready to play: " + ready);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!(other instanceof WaitingRoomInfo)) {
            return false;
        }
        WaitingRoomInfo otherInfo = (WaitingRoomInfo) other;
        return empty_slots == otherInfo.empty_slots &&
                max_slots == otherInfo.max_slots && ready == otherInfo.ready;
    }

    @Override
    public int hashCode() {
        return ready & empty_slots & max_slots;
    }

    public void set(WaitingRoomInfo other) {
        this.ready = other.ready;
        this.empty_slots = other.empty_slots;
        this.max_slots = other.max_slots;
    }
}
