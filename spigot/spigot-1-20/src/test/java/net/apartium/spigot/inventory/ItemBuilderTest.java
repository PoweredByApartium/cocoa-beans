package net.apartium.spigot.inventory;

import net.apartium.cocoabeans.spigot.inventory.ItemBuilder;
import net.apartium.spigot.SpigotTestBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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

        item = ItemBuilder.builder(Material.DIAMOND)
                .setLore(Component.text("lore test again"))
                .build();

        assertEquals(1, item.getItemMeta().lore().size());
        assertEquals(Component.text("lore test again"), item.lore().get(0));
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

    @Test
    public void addSingleLoreLine() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .addLoreLine("This is lore addition test")
                .build();

        assertEquals(1, item.getItemMeta().getLore().size());
        assertEquals("This is lore addition test", item.getItemMeta().getLore().get(0));

        ItemStack itemEdit = ItemBuilder.builder(item)
                .addLoreLine("This is lore addition test #2!")
                .build();

        assertEquals(2, itemEdit.getItemMeta().getLore().size());
        assertEquals("This is lore addition test", itemEdit.getItemMeta().getLore().get(0));
        assertEquals("This is lore addition test #2!", itemEdit.getItemMeta().getLore().get(1));

    }

    @Test
    public void addMultiplyStringLoreLines() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .addLoreLines(
                        "This is lore addition test with multiply lines",
                        "This lore line is a test!")
                .build();

        assertEquals(2, item.getItemMeta().getLore().size());
        assertEquals("This is lore addition test with multiply lines", item.getItemMeta().getLore().get(0));
        assertEquals("This lore line is a test!", item.getItemMeta().getLore().get(1));

        ItemStack itemEdit = ItemBuilder.builder(item)
                .addLoreLines(
                        "This is lore addition test #3!",
                        "This is lore addition test #4!")
                .build();

        assertEquals(4, itemEdit.getItemMeta().getLore().size());
        assertEquals("This is lore addition test with multiply lines", itemEdit.getItemMeta().getLore().get(0));
        assertEquals("This lore line is a test!", itemEdit.getItemMeta().getLore().get(1));
        assertEquals("This is lore addition test #3!", itemEdit.getItemMeta().getLore().get(2));
        assertEquals("This is lore addition test #4!", itemEdit.getItemMeta().getLore().get(3));
    }

    @Test
    public void addSingleComponentLoreLine() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .addLoreLine(Component.text("This is lore addition test for component"))
                .build();

        assertEquals(1, item.getItemMeta().lore().size());
        assertEquals(Component.text("This is lore addition test for component"), item.getItemMeta().lore().get(0));

        ItemStack itemEdit = ItemBuilder.builder(item)
                .addLoreLine(Component.text("This is lore addition test 2!"))
                .build();

        assertEquals(2, itemEdit.getItemMeta().lore().size());
        assertEquals(Component.text("This is lore addition test for component"), itemEdit.getItemMeta().lore().get(0));
        assertEquals(Component.text("This is lore addition test 2!"), itemEdit.getItemMeta().lore().get(1));
    }

    @Test
    public void addMultiplyComponentLoreLine() {
        ItemStack item = ItemBuilder.builder(Material.DIAMOND)
                .addLoreLines(
                        Component.text("This lore line #1"),
                        Component.text("This lore line #2"))
                .build();

        assertEquals(2, item.getItemMeta().lore().size());
        assertEquals(Component.text("This lore line #1"), item.getItemMeta().lore().get(0));
        assertEquals(Component.text("This lore line #2"), item.getItemMeta().lore().get(1));

        ItemStack itemEdit = ItemBuilder.builder(item)
                .addLoreLine(Component.text("This lore line #3"))
                .addLoreLine(Component.text("This lore line #4"))
                .build();

        assertEquals(4, itemEdit.getItemMeta().lore().size());
        assertEquals(Component.text("This lore line #1"), itemEdit.getItemMeta().lore().get(0));
        assertEquals(Component.text("This lore line #2"), itemEdit.getItemMeta().lore().get(1));
        assertEquals(Component.text("This lore line #3"), itemEdit.getItemMeta().lore().get(2));
        assertEquals(Component.text("This lore line #4"), itemEdit.getItemMeta().lore().get(3));
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
