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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * This class constructs ItemStacks from 0, makes them possibly easier to create,
 * via the building structure and allows easy and flexible ways to create ItemStacks.
 *
 * @author ofirtim
 */
public class ItemBuilder {

    private ItemStack itemToBeCreated;

    private ItemMeta itemMeta;

    private String displayName;

    private Material matType;

    private int amount;

    private Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

    private final int maxStackSize;

    public ItemBuilder(Material matType, String displayName) {
        this.maxStackSize = 64;
        this.amount = 1;
        this.matType = matType;
        this.itemToBeCreated = new ItemStack(matType, amount);
    }

    public ItemBuilder setAmount(int newQuantity) {
        this.amount = newQuantity;
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean unsafe) {
        return this;
    }

    public ItemStack build(boolean force) {
        return new ItemStack()
    }
}
