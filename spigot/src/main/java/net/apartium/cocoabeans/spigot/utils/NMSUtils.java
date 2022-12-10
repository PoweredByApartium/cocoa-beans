/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot.utils;

import org.bukkit.Bukkit;

/**
 * @deprecated Will be replaced with an actually normal util later on. Not for use outside the library itself
 */
public class NMSUtils {

    private static String version;
    private static final Class<?> craftItemStack;
    private static final Class<?> nbtTagList;
    private static final Class<?> nbtTagString;
    private static final Class<?> nbtTagCompound;
    private static final Class<?> itemStack;

    static {
        try {
            craftItemStack = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack");
            nbtTagList = Class.forName("net.minecraft.nbt.NBTTagList");
            nbtTagString = Class.forName("net.minecraft.nbt.NBTTagString");
            nbtTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
            itemStack = Class.forName("net.minecraft.world.item.ItemStack");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersion() {
        if (version == null)
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return version;
    }

    public static Class<?> getCraftItemStack() {
        return craftItemStack;
    }

    public static Class<?> getNBTTagList() {
        return nbtTagList;
    }

    public static Class<?> getNbtTagString() {
        return nbtTagString;
    }

    public static Class<?> getNBTTagCompound() {
        return nbtTagCompound;
    }

    public static Class<?> getItemStack() {
        return itemStack;
    }
}
