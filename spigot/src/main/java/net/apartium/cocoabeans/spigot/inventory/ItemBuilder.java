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
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chained class used to modify and create item stacks.
 * via the building structure and allows easy and flexible ways to create ItemStacks.
 *
 * @author ofirtim
 */
public class ItemBuilder {

    private ItemStack itemToBeCreated;

    private ItemMeta itemMeta;

    private String displayName;

    private Material matType;

    private int
            maxStackSize,
            amount,
            customModelData;

    private short durability;

    private Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

    private List<String> lore;

    public ItemBuilder(Material matType, String displayName) {
        this.maxStackSize = 64;
        this.amount = 1;
        this.matType = matType;
        this.itemToBeCreated = new ItemStack(matType, amount);
    }

    public ItemBuilder(Material matType, short durability, String displayName) {
    }

    /**
     * Set the amount of the item in the stack
     * @param newQuantity new amount to set
     * @return current instance
     */
    public ItemBuilder amount(int newQuantity) {
        this.amount = newQuantity;
        return this;
    }

    /**
     * Adds an enchantment to the map list to finally enchant the item
     * when is constructed.
     *
     * @param enchantment The enchantment to use
     * @param level The level to set the enchantment to (is allowing unsafe.)
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        enchantmentMap.put(enchantment, level);
        return this;
    }

    /**
     * This method requires to include the {@link EnchantGlow} class.
     * Makes the item glow like its enchanted, but does not actually register
     * the enchantment as anything, simply commits that the item is enchanted.
     *
     * @param isGlowing Set the item to glow, if false this will revoke glow.
     */
    public ItemBuilder setGlowing(boolean isGlowing) {
        enchantmentMap.put(EnchantGlow.getGlow(), 1);
        return this;
    }

    public ItemBuilder setCustomModelId(int id) {
        this.customModelData = id;
        return this;
    }

    /**
     *
     * @param loreLines
     * @return
     */
    public ItemBuilder addLoreLines(String... loreLines) {

        return this;
    }

    /**
     *
     * @param newLoreList
     * @return
     */
    public ItemBuilder addLoreLines(List<String> newLoreList) {
        return this;
    }

    public ItemStack build(boolean force) {
        ItemStack itemStack = this.itemToBeCreated;
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(amount);
        itemStack.addUnsafeEnchantments(enchantmentMap);
        itemMeta.setCustomModelData(customModelData);
        return itemStack;
    }
}
