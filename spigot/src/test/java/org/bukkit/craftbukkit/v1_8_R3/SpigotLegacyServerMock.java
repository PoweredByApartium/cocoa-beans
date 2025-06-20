package org.bukkit.craftbukkit.v1_8_R3;

import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.stubs.MethodMock_v1_8_R3;
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
     * @see NMSUtils#containsVersion(String)
     * @return a fake method stub
     */
    public MethodMock_v1_8_R3 getHandle() {
        return new MethodMock_v1_8_R3();
    }
}
