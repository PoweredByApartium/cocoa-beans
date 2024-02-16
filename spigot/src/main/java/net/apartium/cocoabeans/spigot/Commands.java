/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.Ensures;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Helper class to work with Bukkit's command system
 * @author Voigon
 */
public class Commands {

    private static final MethodHandle getCommandMap = getCommandMap0();

    private static final CommandMap commandMap = getCommandMap(Bukkit.getServer());

    private static MethodHandle getCommandMap0() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
            return MethodHandles.lookup().unreflect(method);
        } catch (Exception e) {
            Dispensers.dispense(e);
            return null;
        }
    }

    /**
     * Get command map instance associated with given Server instance
     * @param server server instance
     * @return command map instance associated with given Server instance
     */
    public static CommandMap getCommandMap(Server server) {
        Ensures.notNull(server, "server +-");
        try {
            return (CommandMap) getCommandMap.invoke(server);
        } catch (Throwable e) {
            Dispensers.dispense(e);
            return null;
        }
    }

    /**
     * Get bukkit command map object
     * @return bukkit command map object
     */
    public static CommandMap getCommandMap() {
        return commandMap;
    }

}
