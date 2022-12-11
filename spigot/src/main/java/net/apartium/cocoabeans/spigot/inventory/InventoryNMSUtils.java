/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot.inventory;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @deprecated Will be replaced with an actually normal util later on. Not for use outside the library itself
 */
@Deprecated
/* package-private */ class InventoryNMSUtils {

    private static String version;
    private static final Class<?> craftItemStack;
    private static final Class<?> nbtTagList;
    private static final Class<?> nbtTagString;
    private static final Class<?> nbtTagCompound;
    private static final Class<?> itemStack;

    private static final Constructor<?> nbtTagCompound_constructors;
    private static final Constructor<?> nbtTagList_constructors;

    private static final Method craftItemStack_asNMSCopy;
    private static final Method craftItemStack_asBukkitCopy;
    private static final Method craftItemStack_u;
    private static final Method nbtTagString_a;
    private static final Method nbtTagString_aString;
    private static final Method nbtTagList_add;
    private static final Method itemStack_a;

    static {
        try {
            // Classes
            craftItemStack = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack");
            nbtTagList = Class.forName("net.minecraft.nbt.NBTTagList");
            nbtTagString = Class.forName("net.minecraft.nbt.NBTTagString");
            nbtTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
            itemStack = Class.forName("net.minecraft.world.item.ItemStack");

            // Constructors
            nbtTagCompound_constructors = nbtTagCompound.getDeclaredConstructors()[0];
            nbtTagList_constructors = nbtTagList.getConstructors()[0];

            // Methods

            craftItemStack_asNMSCopy = craftItemStack.getMethod("asNMSCopy");
            craftItemStack_asBukkitCopy = craftItemStack.getMethod("asBukkitCopy");
            craftItemStack_u = craftItemStack.getMethod("u");

            nbtTagString_a = nbtTagString.getMethod("a", String.class, nbtTagList);
            nbtTagString_aString = nbtTagString.getMethod("a", String.class);

            nbtTagList_add = nbtTagList.getMethod("add", Object.class);

            itemStack_a = itemStack.getMethod("a", nbtTagCompound);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersion() {
        if (version == null)
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return version;
    }

    // Getters

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

    // Constructors

    public static Constructor<?> getNbtTagCompound_constructors() {
        return nbtTagCompound_constructors;
    }

    public static Constructor<?> getNbtTagList_constructors() {
        return nbtTagList_constructors;
    }

    // Methods

    public static Method getCraftItemStack_asNMSCopy() {
        return craftItemStack_asNMSCopy;
    }

    public static Method getCraftItemStack_asBukkitCopy() {
        return craftItemStack_asBukkitCopy;
    }

    public static Method getNbtTagString_a() {
        return nbtTagString_a;
    }

    public static Method getNbtTagString_aString() {
        return nbtTagString_aString;
    }

    public static Method getNbtTagList_add() {
        return nbtTagList_add;
    }

    public static Method getCraftItemStack_u() {
        return craftItemStack_u;
    }

    public static Method getItemStack_a() {
        return itemStack_a;
    }
}
