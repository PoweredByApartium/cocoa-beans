package net.apartium.cocoabeans.spigot.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArmorSlotTest {

    @Test
    void getArmorSlotTypeItemNull() {
        assertNull(ArmorSlot.getArmorSlotType((ItemStack) null));
    }

    @Test
    void getArmorSlotTypeWithHelmetItem() {
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(new ItemStack(Material.DIAMOND_HELMET)));
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(new ItemStack(Material.SKELETON_SKULL)));
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(new ItemStack(Material.CREEPER_HEAD)));
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(new ItemStack(Material.CARVED_PUMPKIN)));
    }

    @Test
    void getArmorSlotTypeWithHelmetMaterial() {
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(Material.DIAMOND_HELMET));
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(Material.SKELETON_SKULL));
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(Material.CREEPER_HEAD));
        assertEquals(ArmorSlot.HELMET, ArmorSlot.getArmorSlotType(Material.CARVED_PUMPKIN));
    }

    @Test
    void getArmorSlotTypeWithChestplateItem() {
        assertEquals(ArmorSlot.CHESTPLATE, ArmorSlot.getArmorSlotType(new ItemStack(Material.DIAMOND_CHESTPLATE)));
        assertEquals(ArmorSlot.CHESTPLATE, ArmorSlot.getArmorSlotType(new ItemStack(Material.ELYTRA)));
    }

    @Test
    void getArmorSlotTypeWithChestplateMaterial() {
        assertEquals(ArmorSlot.CHESTPLATE, ArmorSlot.getArmorSlotType(Material.DIAMOND_CHESTPLATE));
        assertEquals(ArmorSlot.CHESTPLATE, ArmorSlot.getArmorSlotType(Material.ELYTRA));
    }

    @Test
    void getArmorSlotTypeWithLeggingsItem() {
        assertEquals(ArmorSlot.LEGGINGS, ArmorSlot.getArmorSlotType(new ItemStack(Material.IRON_LEGGINGS)));
        assertEquals(ArmorSlot.LEGGINGS, ArmorSlot.getArmorSlotType(new ItemStack(Material.DIAMOND_LEGGINGS)));
    }

    @Test
    void getArmorSlotTypeWithLeggingsMaterial() {
        assertEquals(ArmorSlot.LEGGINGS, ArmorSlot.getArmorSlotType(Material.IRON_LEGGINGS));
        assertEquals(ArmorSlot.LEGGINGS, ArmorSlot.getArmorSlotType(Material.DIAMOND_LEGGINGS));
    }

    @Test
    void getArmorSlotTypeWithBootsItem() {
        assertEquals(ArmorSlot.BOOTS, ArmorSlot.getArmorSlotType(new ItemStack(Material.IRON_BOOTS)));
        assertEquals(ArmorSlot.BOOTS, ArmorSlot.getArmorSlotType(new ItemStack(Material.DIAMOND_BOOTS)));
    }

    @Test
    void getArmorSlotTypeWithBootsMaterial() {
        assertEquals(ArmorSlot.BOOTS, ArmorSlot.getArmorSlotType(Material.IRON_BOOTS));
        assertEquals(ArmorSlot.BOOTS, ArmorSlot.getArmorSlotType(Material.DIAMOND_BOOTS));
    }

    @Test
    void getArmorSlotTypeWoodMaterial() {
        assertNull(ArmorSlot.getArmorSlotType(Material.OAK_WOOD));
    }

    @Test
    void getArmorSlotTypeWoodItem() {
        assertNull(ArmorSlot.getArmorSlotType(new ItemStack(Material.OAK_WOOD)));
    }

    @Test
    void getSlot() {
        assertEquals(5, ArmorSlot.HELMET.getSlot());
        assertEquals(6, ArmorSlot.CHESTPLATE.getSlot());
        assertEquals(7, ArmorSlot.LEGGINGS.getSlot());
        assertEquals(8, ArmorSlot.BOOTS.getSlot());
    }


}
