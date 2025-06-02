package net.apartium.cocoabeans.spigot.listeners;

import net.apartium.cocoabeans.spigot.board.BoardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final BoardManager boardManager;

    public PlayerQuitListener(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        boardManager.unregisterBoard(event.getPlayer().getUniqueId());
    }

}
