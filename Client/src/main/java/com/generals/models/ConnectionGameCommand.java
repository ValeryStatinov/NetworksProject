package com.generals.models;

public class ConnectionGameCommand {
    String command;
    String name = null;

    public ConnectionGameCommand(String command, String name) {
//        if (command != "create_game" || command != "ready_to_start") {
//            throw new Exception("Unknown ConnectionGameCommand");
//        }
        this.command = command;
        this.name = name;
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
