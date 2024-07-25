package net.apartium.spigot.inventory;

import net.apartium.spigot.SpigotTestBase;
import net.apartium.cocoabeans.spigot.inventory.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemBuilderTest extends SpigotTestBase {

    @Override
    public void initialize() {

    }

    @Test
    public void buildTest() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND).build();

        assertItem(item, Material.DIAMOND, 1, (String) null, null);
        assertItem(item, Material.DIAMOND, 1, (Component) null, null);
    }

    @Test
    public void setDisplayName() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .setDisplayName("Diamond test").build();

        assertItem(item, Material.DIAMOND, 1, "Diamond test", null);

        item = ItemBuilder.builder(Material.DIAMOND)
                .setDisplayName(Component.text("Diamond test")).build();

        assertItem(item, Material.DIAMOND, 1, Component.text("Diamond test"), null);
    }

    @Test
    public void setLore() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .setLore("Lore test", "", "", "Made by kfir").build();

        assertItem(item, Material.DIAMOND, 1, null, List.of("Lore test", "", "", "Made by kfir"));

        item = ItemBuilder.builder(Material.DIAMOND)
                        .setLore(List.of(
                                Component.text("Lore test"),
                                Component.empty(),
                                Component.empty(),
                                Component.text("Made by kfir")
                        )).build();

        assertItem(item, Material.DIAMOND, 1, null, List.of(Component.text("Lore test"), Component.empty(), Component.empty(), Component.text("Made by kfir")));
    }

    @Test
    public void setAmount() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .setAmount(5)
                .build();

        assertItem(item, Material.DIAMOND, 5, (String) null, null);

        item = ItemBuilder.builder(Material.DIAMOND)
                .setAmount(25)
                .build();

        assertItem(item, Material.DIAMOND, 25, (String) null, null);
    }

    @Test
    public void setDurability() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .setDurability((short) 5)
                .build();

        assertItem(item, Material.DIAMOND, 1, (String) null, null);
        assertItem(item, Material.DIAMOND, 1, (String) null, null);

        assertEquals((short) 5, item.getDurability());
    }

    @Test
    public void unbreakable() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .setUnbreakable(true)
                .build();

        assertTrue(item.getItemMeta().isUnbreakable());

        item = ItemBuilder.builder(Material.DIAMOND)
                .setUnbreakable(false)
                .build();

        assertFalse(item.getItemMeta().isUnbreakable());
    }

    public void assertItem(ItemStack item, Material type, int amount, String name, List<String> lore) {
        assertEquals(type, item.getType());
        assertEquals(amount, item.getAmount());
        assertEquals(name, item.getItemMeta().getDisplayName());
        assertEquals(lore, item.getItemMeta().getLore());
    }

    public void assertItem(ItemStack item, Material type, int amount, Component name, List<Component> lore) {
        assertEquals(type, item.getType());
        assertEquals(amount, item.getAmount());
        assertEquals(name, item.getItemMeta().displayName());

        if (lore == null)
            assertFalse(item.getItemMeta().hasLore());
        else {
            assertTrue(item.getItemMeta().hasLore());
            assertEquals(lore, item.getItemMeta().lore());
        }
    }



}
