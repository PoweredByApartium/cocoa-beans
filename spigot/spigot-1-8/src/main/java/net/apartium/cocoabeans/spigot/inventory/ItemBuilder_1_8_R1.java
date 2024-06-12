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

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ItemBuilder_1_8_R1 extends ItemBuilder {

    @ApiStatus.Internal
    public ItemBuilder_1_8_R1(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public ItemBuilder setOwningPlayer(OfflinePlayer offlinePlayer) {
        if (!(meta instanceof SkullMeta)) return this;
        PlayerProfile playerProfile = Bukkit.createProfile(offlinePlayer.getUniqueId());
        if (!playerProfile.completeFromCache(true))
            playerProfile.complete(true);
        return setSkullProfile(playerProfile);
    }

    @Override
    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    @Override
    public ItemBuilder setUnbreakable(boolean value) {
        meta.spigot().setUnbreakable(value);
        return this;
    }

}
