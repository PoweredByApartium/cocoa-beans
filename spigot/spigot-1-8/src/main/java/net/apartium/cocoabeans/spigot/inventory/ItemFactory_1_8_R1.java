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
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.net.MalformedURLException;
import java.net.URL;

public class ItemFactory_1_8_R1 implements ItemFactory {
    @Override
    public ItemBuilder builder(ItemStack itemStack) {
        return new ItemBuilder_1_8_R1(itemStack);
    }

    @Override
    public ItemBuilder builder(Material material) {
        return new ItemBuilder_1_8_R1(new ItemStack(material));
    }

    @Override
    public ItemBuilder skullBuilder(OfflinePlayer offlinePlayer) {
        ItemBuilder builder = builder(Material.SKULL_ITEM);
        builder.setDurability((short) 3);
        builder.setOwingPlayer(offlinePlayer);
        return builder;
    }

    @Override
    public ItemBuilder skullBuilder(URL url) {
        ItemBuilder builder = builder(Material.SKULL_ITEM);
        builder.setDurability((short) 3);
        builder.setSkullTextureURL(url);
        return builder;
    }

    @Override
    public ItemBuilder skullBuilder(String base64) {
        ItemBuilder builder = builder(Material.SKULL_ITEM);
        builder.setDurability((short) 3);
        try {
            builder.setSkullTextureBase64(base64);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return builder;
    }
}
