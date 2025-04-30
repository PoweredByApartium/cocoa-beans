package net.apartium.cocoabeans.spigot.board;

import net.apartium.cocoabeans.scoreboard.CocoaBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardManager {

    private final Map<UUID, CocoaBoard> boards = new HashMap<>();

    public CocoaBoard getBoard(UUID uuid) {
        return boards.get(uuid);
    }

    public void registerBoard(UUID targetUUID, CocoaBoard board) {
        boards.putIfAbsent(targetUUID, board);
    }

    public void unregisterBoard(UUID targetUUID) {
        boards.remove(targetUUID);
    }

    public void heartbeat() {
        for (CocoaBoard board : boards.values())
            board.heartbeat();
    }
}
