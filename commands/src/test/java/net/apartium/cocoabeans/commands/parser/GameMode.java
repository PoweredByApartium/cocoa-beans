package net.apartium.cocoabeans.commands.parser;

public enum GameMode {
    CREATIVE(1),
    SURVIVAL(0),
    ADVENTURE(2),
    SPECTATOR(3);

    private final int value;

    GameMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
