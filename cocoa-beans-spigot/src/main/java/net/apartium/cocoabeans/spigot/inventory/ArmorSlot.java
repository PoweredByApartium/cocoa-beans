/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Represents a possible armor slot, for example helmet or boots
 */
public enum ArmorSlot {

    HELMET(5),
    CHESTPLATE(6),
    LEGGINGS(7),
    BOOTS(8);

    private final int slot;

    ArmorSlot(int slot) {
        this.slot = slot;
    }

    /**
     * Return matching armor slot for given item stack based on type
     * @param item item stack instance
     * @return armor slot if any, otherwise null
     */
    @Nullable
    public static ArmorSlot getArmorSlotType(ItemStack item) {
        if (item == null)
            return null;

        return getArmorSlotType(item.getType());
    }

    /**
     * Return matching armor slot for given item stack based on type
     * @param type item stack material
     * @return armor slot if any, otherwise null
     */
    @Nullable
    public static ArmorSlot getArmorSlotType(Material type) {
        String typeName = type.name();

        if (typeName.endsWith("_HELMET") || typeName.endsWith("_SKULL") ||
                typeName.endsWith("_HEAD") || typeName.endsWith("CARVED_PUMPKIN")) return HELMET;

        if (typeName.endsWith("_CHESTPLATE") || typeName.equals("ELYTRA")) return CHESTPLATE;
        if (typeName.endsWith("_LEGGINGS")) return LEGGINGS;
        if (typeName.endsWith("_BOOTS")) return BOOTS;
        return null;
    }

    /**
     * Get inventory slot associated with armor slot
     * @return inventory slot associated with armor slot
     */
    public int getSlot() {
        return slot;
    }

}