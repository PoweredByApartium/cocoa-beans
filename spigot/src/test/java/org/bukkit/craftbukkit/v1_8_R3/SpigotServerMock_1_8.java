package org.bukkit.craftbukkit.v1_8_R3;

import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.stubs.MethodMock_v1_8_R3;
import org.bukkit.craftbukkit.SpigotServerMock;

/**
 * A ServerMock implementation for checking code that requires 'older' server version
 * @see NMSUtils
 * @see net.apartium.cocoabeans.spigot.utils.NMSUtilsTest
 * @see net.apartium.cocoabeans.spigot.utils.NMSUtilsTest1_8
 */
public class SpigotServerMock_1_8 extends SpigotServerMock {

    /**
     * A method stub for the getHandle method
     * @see NMSUtils#containsVersion(String)
     * @return a fake method stub
     */
    public MethodMock_v1_8_R3 getHandle() {
        return new MethodMock_v1_8_R3();
    }
}
