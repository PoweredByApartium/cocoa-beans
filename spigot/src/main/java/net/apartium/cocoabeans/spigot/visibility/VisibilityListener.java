package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/* package-private */ class VisibilityListener implements Listener {

    private final VisibilityManager manager;

    /* package-private */ final VisibilityPlayerRemoveType removeType;

    public VisibilityListener(VisibilityManager manager, VisibilityPlayerRemoveType removeType) {
        this.manager = manager;
        this.removeType = removeType;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        manager.handlePlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent event) {
        if (removeType != VisibilityPlayerRemoveType.ON_LEAVE)
            return;

        manager.removePlayer(event.getPlayer().getUniqueId());
    }

}
