package org.bukkit.craftbukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.stubs.MethodMock;
import net.apartium.cocoabeans.spigot.utils.NMSUtilsLegacyTest;
import net.apartium.cocoabeans.spigot.utils.NMSUtilsModernTest;

/**
 * A ServerMock implementation for checking code that requires 'newer' server version
 * @see NMSUtils
 * @see NMSUtilsModernTest
 * @see NMSUtilsLegacyTest
 */
public class SpigotModernServerMock extends ServerMock {

    /**
     * A method stub for the getHandle method
     * @see NMSUtils#containsVersion(String)
     * @return a fake method stub
     */
    public MethodMock getHandle() {
        return new MethodMock();
    }
}