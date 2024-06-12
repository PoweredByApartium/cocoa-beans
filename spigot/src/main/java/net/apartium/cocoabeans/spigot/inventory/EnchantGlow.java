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

import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;

/**
 * Add glowing enchant to your items
 */
public class EnchantGlow {

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

		MinecraftVersion minecraftVersion = ServerUtils.getVersion();
		if (minecraftVersion.update() >= 8 && minecraftVersion.update() <= 12) {
			return ItemFactoryInstantiator.construct("EnchantGlow_1_8_R1", Enchantment.class);
		} else if (minecraftVersion.update() == 20) {
			return ItemFactoryInstantiator.construct("EnchantGlow_1_20_R1", Enchantment.class);
		} else {
			throw new RuntimeException("Unsupported server version " + minecraftVersion);
		}
	}

}
