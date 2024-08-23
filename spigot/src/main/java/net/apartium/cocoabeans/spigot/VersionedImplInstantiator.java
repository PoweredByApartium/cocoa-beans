/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.spigot.inventory.ItemFactory;
import net.apartium.cocoabeans.spigot.inventory.ItemUtilsHelpers;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class VersionedImplInstantiator {

    public static ItemUtilsHelpers createItemUtilsHelpers() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> createItemUtilsHelper("ItemUtilsHelpers_1_8_R1");
            default -> createItemUtilsHelper("ItemUtilsHelpers_1_20_R1");
        };
    }

    public static ItemFactory createItemFactory() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructItemFactory("ItemFactory_1_8_R1");
            default -> constructItemFactory("ItemFactory_1_20_R1");
        };
    }

    public static Enchantment createGlow() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructEnchantGlow("EnchantGlow_1_8_R1");
            default -> constructEnchantGlow("EnchantGlow_1_20_R1");
        };
    }

    private static ItemFactory constructItemFactory(String clazz) {
        return construct(clazz, ItemFactory.class);
    }

    private static Enchantment constructEnchantGlow(String clazz) {
        return construct(clazz, Enchantment.class);
    }

    private static ItemUtilsHelpers createItemUtilsHelper(String clazz) {
        return construct(clazz, ItemUtilsHelpers.class);
    }

    /* package-private */ static <T> T construct(String name, Class<T> type) {
        try {
            Class<? extends T> cls = Class.forName(String.format("net.apartium.cocoabeans.spigot.inventory.%s", name), true, ItemFactory.class.getClassLoader())
                    .asSubclass(type);
            return cls.getConstructor().newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
