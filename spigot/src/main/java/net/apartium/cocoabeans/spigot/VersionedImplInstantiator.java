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
import net.apartium.cocoabeans.spigot.visibility.PlayerVisibilityController;
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
            case 8 -> createItemUtilsHelper("inventory.ItemUtilsHelpers_1_8_R1");
            default -> createItemUtilsHelper("inventory.ItemUtilsHelpers_1_20_R1");
        };
    }

    public static ItemFactory createItemFactory() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructItemFactory("inventory.ItemFactory_1_8_R1");
            default -> constructItemFactory("inventory.ItemFactory_1_20_R1");
        };
    }

    public static Enchantment createGlow() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructEnchantGlow("inventory.EnchantGlow_1_8_R1");
            default -> constructEnchantGlow("inventory.EnchantGlow_1_20_R1");
        };
    }

    /**
     * Creates player visibility controller by server version
     * @return player visibility controller by server version
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static PlayerVisibilityController createPlayerVisibilityController() {
        MinecraftVersion minecraftVersion = ServerUtils.getVersion();
        return switch (minecraftVersion.update()) {
            case 8 -> constructPlayerVisibilityController("visibility.PlayerVisibilityController_1_8_R1");
            default -> constructPlayerVisibilityController("visibility.PlayerVisibilityController_1_20_R1");
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

    private static PlayerVisibilityController constructPlayerVisibilityController(String clazz) {
        return construct(clazz, PlayerVisibilityController.class);
    }

    public static <T> T construct(String name, Class<T> type) {
        try {
            Class<? extends T> cls = Class.forName(String.format("net.apartium.cocoabeans.spigot.%s", name), true, ItemFactory.class.getClassLoader())
                    .asSubclass(type);
            return cls.getConstructor().newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
