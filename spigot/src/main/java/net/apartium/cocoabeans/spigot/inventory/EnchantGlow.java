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

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Add glowing enchant to your items
 */
public class EnchantGlow extends Enchantment {

	public static final NamespacedKey
			ENCHANT_GLOW_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(EnchantGlow.class), "glow");

	public static final Enchantment ENCHANT_GLOW = getGlow();

	private static Enchantment getGlow() {
		try {
			Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
			acceptingNew.setAccessible(true);
			acceptingNew.set(null, true);
			acceptingNew.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JavaPlugin plugin = JavaPlugin.getProvidingPlugin(EnchantGlow.class);
		EnchantGlow glow = new EnchantGlow(ENCHANT_GLOW_KEY);
		if (Enchantment.getByKey(ENCHANT_GLOW_KEY) == null)
			Enchantment.registerEnchantment(glow);
		return glow;
	}

	public EnchantGlow(NamespacedKey id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public int getMaxLevel() {
		return 10;
	}

	@Override
	public String getName() {
		return "Glow";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}
	
	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	public @NotNull Component displayName(int level) {
		return null;
	}

	public boolean isTradeable() {
		return false;
	}

	public boolean isDiscoverable() {
		return false;
	}

	public float getDamageIncrease(int level, @NotNull EntityCategory entityCategory) {
		return 0;
	}

	public @NotNull Set<EquipmentSlot> getActiveSlots() {
		return null;
	}

	public @NotNull String translationKey() {
		return null;
	}
}
