package net.apartium.cocoabeans.spigot.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.parsers.SourceParser;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import net.apartium.cocoabeans.schematic.AbstractSchematic;
import net.apartium.cocoabeans.schematic.MeowSchematicFactory;
import net.apartium.cocoabeans.schematic.SimpleBlockDataEncoder;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import net.apartium.cocoabeans.schematic.format.CocoaSchematicFormat;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.TestCocoaBeansSpigotLoader;
import net.apartium.cocoabeans.spigot.inventory.ItemBuilder;
import net.apartium.cocoabeans.spigot.schematic.SpigotSchematic;
import net.apartium.cocoabeans.spigot.schematic.SpigotSchematicHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;


@Command("schematic")
public class SchematicCommand implements CommandNode, Listener {

    public static final Component WAND_NAME = Component.text("Schematic wand", NamedTextColor.GOLD);

    private final Map<String, SpigotSchematic> schematics = new HashMap<>();
    private final TestCocoaBeansSpigotLoader plugin;

    private final Map<UUID, SchematicSettings> settings = new HashMap<>();
    private final CocoaSchematicFormat format;

    public SchematicCommand(TestCocoaBeansSpigotLoader plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();

        dataFolder = new File(dataFolder, "schematics");
        if (!dataFolder.exists())
            dataFolder.mkdir();


        this.format = new CocoaSchematicFormat(
                Map.of(
                        SimpleBlockDataEncoder.id, new SimpleBlockDataEncoder(Map.of())
                ),
                Set.of(
                        CompressionEngine.raw(), CompressionEngine.gzip()
                ),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new MeowSchematicFactory()
        );

        for (File file : dataFolder.listFiles()) {
            if (!file.getName().endsWith(".cbschem"))
                continue;

            try {
                AbstractSchematic schematic = (AbstractSchematic) this.format.read(SeekableInputStream.open(
                        file.toPath()
                ));

                schematics.put(schematic.title(), new SpigotSchematic(schematic, schematic.size(), schematic.axisOrder()));
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Error reading schematic file " + file.getAbsolutePath(), e);
            }
        }
    }

    @SourceParser(keyword = "schematic", clazz = SpigotSchematic.class, resultMaxAgeInMills = 0)
    public Map<String, SpigotSchematic> schematics() {
        return schematics;
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("wand")
    public void wand(Player player) {
        player.getInventory().addItem(
                ItemBuilder.builder(Material.WOODEN_AXE)
                        .setDisplayName(WAND_NAME)
                        .build()
        );
        player.sendMessage(Component.text("You now have a wand!", NamedTextColor.YELLOW));
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("paste <schematic>")
    public void paste(Player player, SpigotSchematic schematic) {
        player.sendMessage("About to paste " + schematic.title());
        schematic.paste(player.getLocation());
        player.sendMessage(Component.text("You paste " + schematic.title(), NamedTextColor.GREEN));
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("save <string>")
    public void save(Player player, String name) {
        SchematicSettings schematicSettings = settings.get(player.getUniqueId());
        if (schematicSettings == null || !schematicSettings.hasCompleteSettings()) {
            player.sendMessage("§cYou didn't specify a settings for the schematic!");
            return;
        }

        if (this.schematics.containsKey(name)) {
            player.sendMessage("§cYou already have a schematic named " + name + "!");
            return;
        }

        player.sendMessage("Load 1");
        SpigotSchematic schematic = SpigotSchematicHelper.load(name, player.getName(), schematicSettings.world, schematicSettings.pos0, schematicSettings.pos1);
        this.schematics.put(name, schematic);

        player.sendMessage("Save 1");
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();

        dataFolder = new File(dataFolder, "schematics");
        if (!dataFolder.exists())
            dataFolder.mkdir();

        File file = new File(dataFolder, name + ".cbschem");
        if (file.exists())
            file.delete();

        try {
            this.format.write(schematic, SeekableOutputStream.open(file.toPath()));
        } catch (IOException e) {
            player.sendMessage("§cError opening schematic file " + file.getName());
            return;
        }
        player.sendMessage("§aSchematic has been saved!");
    }

    @SubCommand("list")
    public void list(CommandSender sender) {
        sender.sendMessage(Component.text("Schematic List", Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD)));
        sender.sendMessage(Component.text("===================================", NamedTextColor.GRAY));
        for (Map.Entry<String, SpigotSchematic> entry : schematics.entrySet()) {
            sender.sendMessage(
                    Component.text()
                            .append(Component.text(entry.getKey(), NamedTextColor.RED))
                            .append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(entry.getValue().id().toString(), NamedTextColor.YELLOW))
                            .build()
            );
        }
        sender.sendMessage(Component.text("===================================", NamedTextColor.GRAY));
    }

    public static class SchematicSettings {

        private Position pos0 = null;
        private Position pos1 = null;
        private World world;

        public boolean hasCompleteSettings() {
            if (pos0 == null)
                return false;

            if (pos1 == null)
                return false;

            return world != null;
        }

        public void setPos0(Player player, Position pos) {
            if (player.getWorld() != world) {
                this.pos1 = null;
                this.world = player.getWorld();
            }

            this.pos0 = pos;
            player.sendMessage(Component.text()
                    .append(Component.text("Position 0 as been set to ", NamedTextColor.YELLOW))
                    .append(Component.text(pos.getX() + ", " + pos.getY() + ", " + pos.getZ(), NamedTextColor.RED))
                    .append(Component.text(this.pos1 == null
                            ? ""
                            : "(" + ((Math.abs(this.pos1.getX() - this.pos0.getX()) + 1) * (Math.abs(this.pos1.getY() - this.pos0.getY()) + 1) * (Math.abs(this.pos1.getZ() - this.pos0.getZ()) + 1)) + ")"
                            , NamedTextColor.DARK_RED))
                    .build()
            );
        }

        public void setPos1(Player player, Position pos) {
            if (player.getWorld() != world) {
                this.pos0 = null;
                this.world = player.getWorld();
            }

            this.pos1 = pos;
            player.sendMessage(Component.text()
                    .append(Component.text("Position 1 as been set to ", NamedTextColor.YELLOW))
                    .append(Component.text(pos.getX() + ", " + pos.getY() + ", " + pos.getZ(), NamedTextColor.RED))
                    .append(Component.text(this.pos0 == null
                                    ? ""
                                    : "(" + ((Math.abs(this.pos1.getX() - this.pos0.getX()) + 1) * (Math.abs(this.pos1.getY() - this.pos0.getY()) + 1) * (Math.abs(this.pos1.getZ() - this.pos0.getZ()) + 1)) + ")"
                            , NamedTextColor.DARK_RED))
                    .build()
            );
        }

        public Position getPos0() {
            return pos0;
        }

        public Position getPos1() {
            return pos1;
        }

        public World getWorld() {
            return world;
        }

    }

    // Listener

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&  event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR)
            return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isWand(item)) {
            item = player.getInventory().getItemInOffHand();
            if (!isWand(item))
                return;
        }

        event.setCancelled(true);

        SchematicSettings schematicSettings = this.settings.computeIfAbsent(player.getUniqueId(), s -> new SchematicSettings());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            schematicSettings.setPos1(player, new Position(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()));
            return;
        }

        schematicSettings.setPos0(player, new Position(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()));
    }

    private boolean isWand(ItemStack item) {
        if (item.getType() != Material.WOODEN_AXE)
            return false;

        if (!item.getItemMeta().hasDisplayName())
            return false;

        return WAND_NAME.equals(item.getItemMeta().displayName());
    }

}
