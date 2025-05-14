package net.apartium.cocoabeans.commands.spigot.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private static PlayerManager instance = null;

    public static PlayerManager getInstance() {
        return instance == null ? (instance = new PlayerManager()) : instance;
    }

    private final Map<UUID, GamePlayer> gamePlayers = new HashMap<>();

    private PlayerManager() {

    }

    public GamePlayer getGamePlayer(UUID uuid) {
        return gamePlayers.get(uuid);
    }

    public GamePlayer createGamePlayer(UUID uuid) {
        return gamePlayers.computeIfAbsent(uuid, (key) -> new GamePlayer(uuid));
    }

    public void removeGamePlayer(UUID uuid) {
        GamePlayer remove = gamePlayers.remove(uuid);
        if (remove != null) {
            remove.setGame(null);
        }
    }

}
