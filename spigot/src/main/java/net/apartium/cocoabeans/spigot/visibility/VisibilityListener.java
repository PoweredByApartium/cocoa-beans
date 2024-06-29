package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/* package-private */ class VisibilityListener implements Listener {

    private final VisibilityManager manager;

    public VisibilityListener(VisibilityManager manager) {
        this.manager = manager;
    }

    public VisibilityManager getManager() {
        return manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        manager.handlePlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent event) {
        if (manager.getRemoveType() != VisibilityManager.VisibilityPlayerRemoveType.ON_LEAVE)
            return;

        manager.removePlayer(event.getPlayer().getUniqueId());
    }

}
