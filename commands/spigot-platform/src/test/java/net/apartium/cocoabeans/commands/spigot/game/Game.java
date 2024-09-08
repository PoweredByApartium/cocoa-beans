package net.apartium.cocoabeans.commands.spigot.game;

import java.util.*;

public class Game {

    private final UUID uniqueId;
    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();
    private Stage stage = Stage.WAITING_FOR_PLAYERS;

    public Game(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }


    public void setStage(Stage stage) {
        this.stage = stage;

        if (stage == Stage.END) {
            gamePlayers.forEach((uuid, gamePlayer) -> gamePlayer.setGame(null));
            gamePlayers.clear();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Collection<GamePlayer> getGamePlayers() {
        return gamePlayers.values();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
