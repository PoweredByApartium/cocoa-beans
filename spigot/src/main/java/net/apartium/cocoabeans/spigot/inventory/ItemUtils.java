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

import net.apartium.cocoabeans.spigot.VersionedImplInstantiator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Various utils for working with item stacks
 * @author Thebotgame
 */
public class ItemUtils {

    private static final ItemUtilsHelpers helpers = VersionedImplInstantiator.createItemUtilsHelpers();

    /**
     * Check if given item stack is an armor piece
     * @param item item stack
     * @return true if armor piece, else false
     */
    public static boolean isArmor(ItemStack item) {
        return ArmorSlot.getArmorSlotType(item) != null;
    }

    /**
     * Check if given item stack is air or is null
     * @param item given item stack
     * @return true if air or null, else false
     */
    public static boolean isAirOrNull(final ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    /**
     * Returns the internal name of the material
     * For 1.8, it will be {@link Material#name()}
     * For 1.13+, it will be {@link Material#getKey()}
     * @param material material to get the internal name of
     * @return internal name of the material
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static String getInternalName(Material material) {
        return helpers.name(material);
    }


}
