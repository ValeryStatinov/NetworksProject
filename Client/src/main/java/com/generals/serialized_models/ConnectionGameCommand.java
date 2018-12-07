package com.generals.serialized_models;

public class ConnectionGameCommand {
    String command;
    String name = null;
    Integer game_id = null;

    public ConnectionGameCommand(String command) {
        this.command = command;
    }

    public void setGameId(int game_id) {
        this.game_id = game_id;
    }

    public void setName(String name) {
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
        if (game_id != null) {
            result.append(" #" + game_id.toString());
        }
        return result.toString();
    }
}
