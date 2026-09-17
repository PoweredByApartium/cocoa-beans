package net.apartium.spigot.inventory;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.apartium.cocoabeans.spigot.inventory.EnchantGlow;
import net.apartium.cocoabeans.spigot.inventory.EnchantGlow_1_20_R1;
import net.apartium.spigot.SpigotTestBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EnchantGlowTest extends SpigotTestBase {

    @Override
    public void initialize() {

    }

    @Test
    void testHasInstance() {
        assertNotNull(EnchantGlow.ENCHANT_GLOW);
        assertInstanceOf(EnchantGlow_1_20_R1.class, EnchantGlow.ENCHANT_GLOW);
    }

    @Test
    void testEnchantGlow() {
        assertNotNull(EnchantGlow.ENCHANT_GLOW);
        assertEquals("cocoa_glow", EnchantGlow.ENCHANT_GLOW.getKey().getKey());
        assertEquals("mockplugin", EnchantGlow.ENCHANT_GLOW.getKey().getNamespace());
    }

    @Test
    void canEnchantItem() {
        assertTrue(EnchantGlow.ENCHANT_GLOW.canEnchantItem(new ItemStack(Material.AIR)));
    }

    @Test
    void displayName() {
        assertEquals(Component.empty(), EnchantGlow.ENCHANT_GLOW.displayName(0));
    }

    @Test
    void conflictsWith() {
        assertFalse(EnchantGlow.ENCHANT_GLOW.conflictsWith(EnchantGlow.ENCHANT_GLOW));
        assertFalse(EnchantGlow.ENCHANT_GLOW.conflictsWith(Enchantment.DAMAGE_ALL));
    }

    @Test
    void getItemTarget() {
        assertEquals(EnchantmentTarget.ALL, EnchantGlow.ENCHANT_GLOW.getItemTarget());
    }

    @Test
    void getMaxLevel() {
        assertEquals(10, EnchantGlow.ENCHANT_GLOW.getMaxLevel());
    }

    @Test
    void getName() {
        assertEquals("CocoaGlow", EnchantGlow.ENCHANT_GLOW.getName());
    }

    @Test
    void getStartLevel() {
        assertEquals(1, EnchantGlow.ENCHANT_GLOW.getStartLevel());
    }

    @Test
    void isTreasure() {
        assertFalse(EnchantGlow.ENCHANT_GLOW.isTreasure());
    }

    @Test
    void isCursed() {
        assertFalse(EnchantGlow.ENCHANT_GLOW.isCursed());
    }

    @Test
    void isTradeable() {
        assertFalse(EnchantGlow.ENCHANT_GLOW.isTradeable());
    }

    @Test
    void isDiscoverable() {
        assertFalse(EnchantGlow.ENCHANT_GLOW.isDiscoverable());
    }

    @Test
    void getRarity() {
        assertEquals(EnchantmentRarity.COMMON, EnchantGlow.ENCHANT_GLOW.getRarity());
    }

    @Test
    void getDamageIncrease() {
        assertEquals(0, EnchantGlow.ENCHANT_GLOW.getDamageIncrease(0, EntityCategory.ARTHROPOD));
    }

    @Test
    void getActiveSlots() {
        assertEquals(Set.of(), EnchantGlow.ENCHANT_GLOW.getActiveSlots());
    }

    @Test
    void translationKey() {
        assertEquals(Enchantment.CHANNELING.translationKey(), EnchantGlow.ENCHANT_GLOW.translationKey());
    }

}
