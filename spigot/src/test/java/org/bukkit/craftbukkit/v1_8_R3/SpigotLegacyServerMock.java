package org.bukkit.craftbukkit.v1_8_R3;

import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.ServerInfoStore;
import net.apartium.cocoabeans.spigot.craftserver.SpigotCraftServer_v1_8_R3;
import net.apartium.cocoabeans.spigot.utils.NMSUtilsLegacyTest;
import net.apartium.cocoabeans.spigot.utils.NMSUtilsModernTest;
import org.bukkit.craftbukkit.SpigotModernServerMock;

/**
 * A ServerMock implementation for checking code that requires 'older' server version
 * @see NMSUtils
 * @see NMSUtilsModernTest
 * @see NMSUtilsLegacyTest
 */
public class SpigotLegacyServerMock extends SpigotModernServerMock {

    /**
     * A method stub for the getHandle method
     * @see ServerInfoStore#containsVersion()
     * @return a fake method stub
     */
    public SpigotCraftServer_v1_8_R3 getHandle() {
        return new SpigotCraftServer_v1_8_R3();
    }
}
