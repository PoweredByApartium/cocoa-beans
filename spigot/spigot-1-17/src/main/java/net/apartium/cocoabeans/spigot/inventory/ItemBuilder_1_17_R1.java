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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;


/**
 * Chained class used to modify and create item stacks.
 * via the building structure and allows easy and flexible ways to create ItemStacks.
 *
 * @author Thebotgame, ofirtim
 */
public class ItemBuilder_1_17_R1 extends ItemBuilder {

    /**
     * Construct a new ItemBuilder instance based on given item stack
     *
     * @param item item stack to start from, given instance will be cloned and not modified
     */
    ItemBuilder_1_17_R1(ItemStack item) {
        super(item);
    }

    public ItemBuilder setDurability(short durability) {
        if (!(meta instanceof Damageable damageable)) return this;
        damageable.setDamage(durability);
        return this;
    }

}
