package org.bukkit.craftbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.ServerInfoStore;
import net.apartium.cocoabeans.spigot.craftserver.SpigotCraftServer;

/**
 * A ServerMock implementation for checking code that requires 'newer' server version
 * @see NMSUtils
 */
public class SpigotModernServerMock extends ServerMock {

    /**
     * A method stub for the getHandle method
     * @see ServerInfoStore#containsVersion()
     * @return a fake method stub
     */
    public SpigotCraftServer getHandle() {
        return new SpigotCraftServer();
    }
}