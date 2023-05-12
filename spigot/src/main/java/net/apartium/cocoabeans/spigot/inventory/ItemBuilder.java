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

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.ApiStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Chained class used to modify and create item stacks.
 * via the building structure and allows easy and flexible ways to create ItemStacks.
 *
 * @author Thebotgame, ofirtim
 */
public class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    /**
     * Construct a new ItemBuilder instance based on given item stack
     * @param item item stack to start from, given instance will be cloned and not modified
     */
    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = item.getItemMeta();
    }

    /**
     * Create player skull of given player
     * @param player player
     */
    public ItemBuilder(OfflinePlayer player) {
        this.item = new ItemStack(Material.PLAYER_HEAD);
        this.meta = item.getItemMeta();

        ((SkullMeta) meta).setOwningPlayer(player);
    }

    /**
     * Construct a new item builder instance
     * @param material type of item
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    /**
     * @param base64 set skull texture with base64
     * @return current instance
     */
    public ItemBuilder setSkullTextureBase64(String base64) {
        if (!(meta instanceof SkullMeta skullMeta)) return this;

        String decodeText = new String(Base64.getDecoder().decode(base64));

        int start = decodeText.indexOf("http"), end = decodeText.indexOf("\"}}}");
        if (start == -1 || end == -1) return this;
        setSkullTextureURL(decodeText.substring(start, end));

        return this;
    }

    /**
     * @param url set skull texture with url to the texture
     * @return current instance
     */
    public ItemBuilder setSkullTextureURL(String url) {
        if (!(meta instanceof SkullMeta skullMeta)) return this;

        PlayerProfile playerProfile = Bukkit.getServer().createPlayerProfile(UUID.randomUUID(), null);
        try {
            playerProfile.getTextures().setSkin(new URL(url));
        } catch (MalformedURLException e) {
            return this;
        }

        skullMeta.setOwnerProfile(playerProfile);

        return this;
    }

    /**
     * @param texts set lore with texts
     * @return current instance
     */
    public ItemBuilder setLore(String... texts) {
        if (texts == null || texts.length == 0) {
            meta.setLore(List.of());
            return this;
        }

        meta.setLore(Arrays.asList(texts));
        return this;
    }

    /**
     * @param lore set lore
     * @return current instance
     */
    public ItemBuilder setLore(List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    /**
     * @param lore set lore
     * @return current instance
     */
    public ItemBuilder setLoreAsText(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    /**
     * @param durability set durability to the item
     * @return current instance
     */
    public ItemBuilder setDurability(short durability) {
        if (!(meta instanceof Damageable damageable)) return this;
        damageable.setDamage(durability);
        return this;
    }


    /**
     * @param component set item name to component name
     * @return current instance
     */

    public ItemBuilder setDisplayName(Component component) {
        meta.displayName(component);
        return this;
    }

    /**
     * @param name set item name to name
     * @return current instance
     */
    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /**
     * set nbt with key type and value
     * @param namespacedKey nbt key
     * @param persistentDataType data type
     * @param object object as value
     * @return current instance
     */
    public ItemBuilder setNBT(NamespacedKey namespacedKey, Object persistentDataType, Object object) {
        meta.getPersistentDataContainer().set(namespacedKey, (PersistentDataType) persistentDataType, object);
        return this;
    }

    /**
     * Set attribute modifiers of the item.
     * @param map attribute modifiers
     * @return current instance
     * @see ItemMeta#setAttributeModifiers(Multimap)
     */
    public ItemBuilder setAttributeModifiers(Multimap<Attribute, AttributeModifier> map) {
        meta.setAttributeModifiers(map);
        return this;
    }

    /**
     * Add attribute modifier the the item.
     * @param attribute attribute
     * @param attributeModifier modifier
     * @return current instance
     * @see ItemMeta#addAttributeModifier(Attribute, AttributeModifier)
     */
    public ItemBuilder addAttributeModifiers(Attribute attribute, AttributeModifier attributeModifier) {
        meta.addAttributeModifier(attribute, attributeModifier);
        return this;
    }

    /**
     * Set the amount of the item in the stack
     * @param  amount new amount to set
     * @return current instance
     */
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Add lines to lore of the item
     * @param lines add lines to lore
     * @return current instance
     */
    public ItemBuilder addLine(String... lines) {
        if (meta.getLore() == null) meta.setLore(Arrays.asList(lines));
        meta.getLore().addAll(List.of(lines));
        return this;
    }

    /**
     * @param text remove line by text
     * @return current instance
     */
    public ItemBuilder removeLine(String text) {
        if (meta.getLore() != null) meta.getLore().remove(text);
        return this;
    }

    /**
     * @param index remove line by index
     * @return current instance
     */
    public ItemBuilder removeLine(int index) {
        if (meta.getLore() == null) return this;

        if (index < 0) return this;
        if (meta.getLore().size() >= index) return this;

        meta.getLore().remove(index);
        return this;
    }

    /**
     * Set custom model data of the item.
     * @param data custom model data
     * @return current instance
     * @see ItemMeta#setCustomModelData(Integer)
     */
    public ItemBuilder setCustomModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    /**
     * Set item color if item meta supports it
     * @param color color to set
     * @return current instance
     * @see LeatherArmorMeta#setColor(Color)
     * @see PotionMeta#setColor(Color)
     * @see FireworkEffect#getColors()
     * @see FireworkEffect#getFadeColors()
     */
    public ItemBuilder setColor(Color color) {
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
            return this;
        } else if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
            return this;
        } else if (meta instanceof FireworkMeta fireworkMeta) {
            for (FireworkEffect fireworkEffect : fireworkMeta.getEffects()) {
                fireworkEffect.getColors().clear();
                fireworkEffect.getColors().add(color);
                fireworkEffect.getFadeColors().clear();
                fireworkEffect.getFadeColors().add(color);
            }

            return this;
        }

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
        if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
            return this;
        }
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Removes an enchantment from the map list to finally enchant the item
     * when is constructed.
     *
     * @param enchantment The enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    /**
     * Sets an enchantment to the map list to finally enchant the item
     * when is constructed.
     *
     * @param enchantments The enchantments to use
     */
    public ItemBuilder setEnchantment(Map<Enchantment, Integer> enchantments) {
        for (Enchantment enchantment : Enchantment.values()) meta.removeEnchant(enchantment);
        for (var entry : enchantments.entrySet()) addEnchantment(entry.getKey(), entry.getValue());
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
        if (isGlowing) addEnchantment   (EnchantGlow.ENCHANT_GLOW, 1);
        else           removeEnchantment(EnchantGlow.ENCHANT_GLOW);
        return this;
    }

    /**
     * Set type of the item.
     * @param material type to set
     * @return current instance
     */
    public ItemBuilder setType(Material material) {
        item.setType(material);
        meta = item.getItemMeta();
        return this;
    }

    /**
     * Set this item as unbreakable or not
     * @param value true for unbreakable, else false
     * @return current instance
     * @see ItemMeta#setUnbreakable(boolean)
     */
    public ItemBuilder setUnbreakable(boolean value) {
        meta.setUnbreakable(value);
        return this;
    }

    /**
     * Add item flags to the item.
     * @param itemFlags item flags to add
     * @return current instance
     */
    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    /**
     * Set potion data if this item stack is a potion, otherwise do nothing
     * @param potionData potion data to set
     * @return current instance
     */
    public ItemBuilder setPotionData(PotionData potionData) {
        if (!(meta instanceof PotionMeta potionMeta)) return this;
        potionMeta.setBasePotionData(potionData);
        return this;
    }

    /**
     * Add potion effect to this item if potion, otherwise do nothing
     * @param potionEffect potion effect to add
     * @return current instance
     */
    public ItemBuilder addPotionEffect(PotionEffect potionEffect) {
        if (!(meta instanceof PotionMeta potionMeta)) return this;
        potionMeta.addCustomEffect(potionEffect, true);
        return this;
    }

    /**
     * Add nbt tag of type string list to the item
     * @param key nbt key
     * @param values values
     * @return current instance
     */
    private ItemBuilder addNbtTagStringList(String key, String... values) {
        item.setItemMeta(meta);

        try {
            Object stack = InventoryNMSUtils.getCraftItemStack_asNMSCopy().invoke(null, item);

            Object idsTag = InventoryNMSUtils.getNbtTagList_constructors().newInstance();
            for (String id : values) {
                InventoryNMSUtils.getNbtTagList_add().invoke(idsTag,
                        InventoryNMSUtils.getNbtTagString_aString().invoke(null, id)
                );
            }

            Object tag =
                    InventoryNMSUtils.getItemStack_u().invoke(stack) != null ?
                            InventoryNMSUtils.getItemStack_u().invoke(stack) :
                            InventoryNMSUtils.getNbtTagCompound_constructors().newInstance();

            InventoryNMSUtils.getNbtTagString_a().invoke(tag, key, idsTag);

            InventoryNMSUtils.getItemStack_a().invoke(null, tag);


            item = (ItemStack) InventoryNMSUtils.getCraftItemStack_asBukkitCopy().
                    invoke(null, stack);
        } catch (Exception e) {
            meta = item.getItemMeta();
            return this;
        }

        meta = item.getItemMeta();
        return this;
    }

    /**
     * Add can destroy flag to the current item
     * @param ids ids
     * @return current instance
     */
    @ApiStatus.Experimental
    public ItemBuilder addCanDestroy(String... ids) {
        addNbtTagStringList("CanDestroy", ids);
        return this;
    }

    /**
     * Add can place on flag to the current item
     * @param ids ids
     * @return current instance
     */
    @ApiStatus.Experimental
    public ItemBuilder addCanPlaceOn(String... ids) {
        addNbtTagStringList("CanPlaceOn", ids);
        return this;
    }

    /**
     * Build current item builder instance and return a copy of produced item
     * @return cloned item stack instance
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item.clone();
    }

}
