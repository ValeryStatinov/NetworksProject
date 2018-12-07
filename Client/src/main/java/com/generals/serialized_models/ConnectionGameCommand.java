package com.generals.serialized_models;

public class ConnectionGameCommand {
    String command;
    String name = null;
    int game_id;

    public ConnectionGameCommand(String command, int id) {
//        if (command != "create_game" || command != "ready_to_start") {
//            throw new Exception("Unknown ConnectionGameCommand");
//        }
        this.command = command;
        this.game_id = id;
    }

    public ConnectionGameCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (command != null) {
            result.append(command.toString());
        }
        if (name != null) {
            result.append(" " + name.toString());
        }
        return result.toString();
    }
}
