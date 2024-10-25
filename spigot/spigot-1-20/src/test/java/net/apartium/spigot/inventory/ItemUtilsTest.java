package net.apartium.spigot.inventory;

import net.apartium.cocoabeans.spigot.inventory.ItemUtils;
import net.apartium.spigot.SpigotTestBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemUtilsTest extends SpigotTestBase {

    @Override
    public void initialize() {
        // nothing needed here
    }

    @Test
    void isArmor() {
        Assertions.assertFalse(ItemUtils.isArmor(null));
        assertTrue(ItemUtils.isArmor(new ItemStack(Material.CHAINMAIL_CHESTPLATE)));
    }

    @Test
    void isAirOrNull() {
        assertTrue(ItemUtils.isAirOrNull(null));
        assertTrue(ItemUtils.isAirOrNull(new ItemStack(Material.AIR)));
        assertFalse(ItemUtils.isAirOrNull(new ItemStack(Material.DIAMOND_SWORD)));
    }

    @Test
    void getInternalName() {
        assertEquals("diamond_sword", ItemUtils.getInternalName(Material.DIAMOND_SWORD));
    }
}
