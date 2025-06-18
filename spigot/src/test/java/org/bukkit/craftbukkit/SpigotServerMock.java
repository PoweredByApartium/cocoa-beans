package org.bukkit.craftbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.spigot.stubs.MethodMock;

/**
 * A ServerMock implementation for checking code that requires 'newer' server version
 * @see net.apartium.cocoabeans.spigot.utils.NMSUtils
 * @see net.apartium.cocoabeans.spigot.utils.NMSUtilsTest
 * @see net.apartium.cocoabeans.spigot.utils.NMSUtilsTest1_8
 */
public class SpigotServerMock extends ServerMock {

    /**
     * A method stub for the getHandle method
     * @see net.apartium.cocoabeans.spigot.utils.NMSUtils#containsVersion(String)
     * @return
     */
    public MethodMock getHandle() {
        return new MethodMock();
    }
}