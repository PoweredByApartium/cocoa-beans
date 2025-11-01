package net.apartium.cocoabeans.spigot.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.parsers.SourceParser;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import net.apartium.cocoabeans.schematic.format.BlockChunkIndexEncoder;
import net.apartium.cocoabeans.schematic.format.CocoaSchematicFormat;
import net.apartium.cocoabeans.schematic.format.SimpleBlockDataEncoder;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.ArrayIntPropFormat;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.schematic.prop.format.ByteBlockPropFormat;
import net.apartium.cocoabeans.schematic.prop.format.IntPropFormat;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.TestCocoaBeansSpigotLoader;
import net.apartium.cocoabeans.spigot.inventory.ItemBuilder;
import net.apartium.cocoabeans.spigot.schematic.SpigotSchematic;
import net.apartium.cocoabeans.spigot.schematic.SpigotSchematicFactory;
import net.apartium.cocoabeans.spigot.schematic.SpigotSchematicHelper;
import net.apartium.cocoabeans.spigot.schematic.SpigotSchematicPlacer;
import net.apartium.cocoabeans.spigot.schematic.prop.BeeHiveHoneyLevelProp;
import net.apartium.cocoabeans.spigot.schematic.prop.BrewingStandBottlesProp;
import net.apartium.cocoabeans.spigot.schematic.prop.LegacyDataProp;
import net.apartium.cocoabeans.spigot.schematic.prop.format.*;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.apartium.cocoabeans.spigot.Locations.toVector;


@Command(value = "schematic", aliases = "schm")
public class SchematicCommand implements CommandNode, Listener {

    public static final Component WAND_NAME = Component.text("Schematic wand", NamedTextColor.GOLD);
    public static final Component INFO_WAND_NAME = Component.text("Info wand", NamedTextColor.GOLD);
    public static final Set<BlockFace> WALL_FACES = Set.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    private final Map<String, SpigotSchematic> schematics = new HashMap<>();
    private final TestCocoaBeansSpigotLoader plugin;

    private final Map<UUID, SchematicSettings> settings = new HashMap<>();
    private final CocoaSchematicFormat format;

    private final Set<SlowBuildSchematic> slowBuild = new HashSet<>();

    public SchematicCommand(TestCocoaBeansSpigotLoader plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();

        dataFolder = new File(dataFolder, "schematics");
        if (!dataFolder.exists())
            dataFolder.mkdir();


        Map<String, BlockPropFormat<?>> propFormatMap = new HashMap<>();

        if (VERSION.isLowerThanOrEqual(MinecraftVersion.V1_12_2)) {
            propFormatMap.put(BlockProp.Legacy.DATA, new ByteBlockPropFormat(LegacyDataProp::new));
            propFormatMap.put(BlockProp.Legacy.SIGN_LINES, BlockPropFormat.ARRAY_STRING);
        } else {
            propFormatMap.put(BlockProp.STAIRS_SHAPE, StairsPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.DIRECTIONAL, DirectionalPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BAMBOO_LEAVES, BambooPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BED_PART, BedPartPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BEEHIVE_HONEY_LEVEL, new IntPropFormat(BeeHiveHoneyLevelProp::new));
            propFormatMap.put(BlockProp.BELL_ATTACHMENT, BellAttachmentPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BIG_DRIP_LEAF_TILT, BigDripleafTiltPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BREWING_STAND_BOTTLES, new ArrayIntPropFormat(BrewingStandBottlesProp::new));
        }

        this.format = new CocoaSchematicFormat(
                Map.of(
                        SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(propFormatMap)
                ),
                Map.of(
                        BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()
                ),
                Set.of(
                        CompressionEngine.raw(), CompressionEngine.gzip()
                ),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new SpigotSchematicFactory()
        );

        for (File file : dataFolder.listFiles()) {
            if (!file.getName().endsWith(".cbschem"))
                continue;

            try {
                SpigotSchematic schematic = (SpigotSchematic) this.format.read(SeekableInputStream.open(
                        file.toPath()
                ));

                schematics.put(schematic.title(), schematic);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Error reading schematic file " + file.getAbsolutePath(), e);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Set<SlowBuildSchematic> toRemove = new HashSet<>();
                for (SlowBuildSchematic slowBuildSchematic : slowBuild) {
                    if (!slowBuildSchematic.pasteOperation().hasNext()) {
                        toRemove.add(slowBuildSchematic);
                        continue;
                    }

                    slowBuildSchematic.pasteOperation().advanceOnSingleAxis(slowBuildSchematic.numsOfBlock());
                }

                for (SlowBuildSchematic slowBuildSchematic : toRemove) {
                    slowBuild.remove(slowBuildSchematic);
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @SourceParser(keyword = "schematic", clazz = SpigotSchematic.class)
    public Map<String, SpigotSchematic> schematics() {
        return schematics;
    }

    @SourceParser(keyword = "axis-order", clazz = AxisOrder.class, resultMaxAgeInMills = -1, ignoreCase = true)
    public Map<String, AxisOrder> axisOrder() {
        return Arrays.stream(AxisOrder.values())
                .collect(Collectors.toMap(
                        axisOrder -> axisOrder.name().toLowerCase(),
                        Function.identity()
                ));
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("wand")
    public void wand(Player player) {
        player.sendMessage(Component.text("Creating a wand", NamedTextColor.YELLOW));
        player.getInventory().addItem(
                ItemBuilder.builder(getWandType())
                        .setDisplayName(WAND_NAME)
                        .build()
        );
        player.sendMessage(Component.text("You now have a wand!", NamedTextColor.YELLOW));
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("wand info")
    public void wandInfo(Player player) {
        player.getInventory().addItem(
                ItemBuilder.builder(Material.BLAZE_ROD)
                        .setDisplayName(INFO_WAND_NAME)
                        .build()
        );
        player.sendMessage(Component.text("You now have a info wand!", NamedTextColor.YELLOW));
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("test info")
    public void info(Player player) {
        Position position = Optional.ofNullable(settings.get(player.getUniqueId()))
                .map(SchematicSettings::getPos0)
                .orElse(null);

        if (position == null) {
            player.sendMessage("§cNo schematic settings found!");
            return;
        }

        Block block = player.getWorld().getBlockAt((int) position.getX(), (int) position.getY(), (int) position.getZ());
        infoBlock(player, block);
    }
    @SenderLimit(SenderType.PLAYER)
    @SubCommand("paste <schematic> rotate <int>")
    public void paste(Player player, SpigotSchematic schematic, int degress) {
        player.sendMessage("About to paste " + schematic.title() + " degress: " + degress);
        SpigotSchematic newSchematic = schematic.toBuilder().rotate(degress).build();
        newSchematic.paste(player.getLocation()).performAll();
        player.sendMessage(Component.text("You paste " + schematic.title(), NamedTextColor.GREEN));
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("paste <schematic> slow <?axis-order> <?int>")
    public void pasteSlow(Player player, SpigotSchematic schematic, Optional<AxisOrder> optAxisOrder, OptionalInt numsOfBlocks) {
        AxisOrder axisOrder = optAxisOrder.orElse(schematic.axisOrder());
        slowBuild.add(new SlowBuildSchematic(
                schematic.paste(player.getLocation(), axisOrder),
                numsOfBlocks.orElse(100)
        ));
        player.sendMessage("Schematic will be placed shortly");
    }

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("paste <schematic>")
    public void paste(Player player, SpigotSchematic schematic) {
        player.sendMessage("About to paste " + schematic.title());
        schematic.paste(player.getLocation()).performAll();
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
        Location location = player.getLocation();
        SpigotSchematic schematic = SpigotSchematicHelper.load(
                name,
                player.getName(),
                new Position(location.getX(), location.getY(), location.getZ()).floor(),
                schematicSettings.world,
                schematicSettings.pos0,
                schematicSettings.pos1,
                SpigotSchematicPlacer.INSTANCE
        );

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

    @Override
    public boolean fallbackHandle(Sender sender, String label, String[] args) {
        sender.sendMessage("Usage: /" + label + " help");
        return true;
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

    public record SlowBuildSchematic(
            PasteOperation pasteOperation,
            int numsOfBlock
    ) {

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
        ItemStack item = player.getInventory().getItemInHand();
        if (item.getType() == Material.BLAZE_ROD) {
            if (INFO_WAND_NAME.equals(item.getItemMeta().displayName())) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    infoBlock(player, clickedBlock);
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (!isWand(item))
            return;


        event.setCancelled(true);

        SchematicSettings schematicSettings = this.settings.computeIfAbsent(player.getUniqueId(), s -> new SchematicSettings());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            schematicSettings.setPos1(player, new Position(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()));
            return;
        }

        schematicSettings.setPos0(player, new Position(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()));
    }

    private static final MinecraftVersion VERSION = ServerUtils.getVersion();

    private Material getWandType() {
        if (VERSION.isLowerThanOrEqual(MinecraftVersion.V1_12_2))
            return Enum.valueOf(Material.class, "WOOD_AXE");

        return Material.WOODEN_AXE;
    }

    private boolean isWand(ItemStack item) {
        if (item.getType() != getWandType())
            return false;

        if (!item.getItemMeta().hasDisplayName())
            return false;

        return WAND_NAME.equals(item.getItemMeta().displayName());
    }

    private void infoBlock(Player player, Block block) {
        Component blockCoordsComponent = Component.text()
                .append(Component.text(block.getX(), NamedTextColor.RED))
                .append(Component.text(", ", NamedTextColor.DARK_GRAY))
                .append(Component.text(block.getY(), NamedTextColor.RED))
                .append(Component.text(", ", NamedTextColor.DARK_GRAY))
                .append(Component.text(block.getZ(), NamedTextColor.RED))
                .hoverEvent(HoverEvent.showText(Component.text("Copy to clipboard", NamedTextColor.YELLOW, TextDecoration.BOLD)))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, block.getX() + ", " +  block.getY() + ", " + block.getZ()))
                .build();

        if (block.getType() == Material.AIR) {
            player.sendMessage(Component.text()
                    .append(Component.text("Block at ", NamedTextColor.YELLOW))
                    .append(blockCoordsComponent)
                    .append(Component.text(" is ", NamedTextColor.YELLOW))
                    .append(Component.text("air", NamedTextColor.RED))
                    .build()
            );
            return;
        }

        player.sendMessage(Component.text()
                .append(Component.text("Block at ", NamedTextColor.YELLOW))
                .append(blockCoordsComponent)
                .append(Component.text(" is ", NamedTextColor.YELLOW))
                .append(Component.text(block.getType().name(), NamedTextColor.RED))
                .build()
        );
        player.sendMessage(Component.text("=".repeat(48), NamedTextColor.DARK_GRAY));
        if (VERSION.isLowerThanOrEqual(MinecraftVersion.V1_12_2)) {
            player.sendMessage(Component.text("§eByte: §c" + block.getData()));
            player.sendMessage(Component.text("=".repeat(48), NamedTextColor.DARK_GRAY));
            return;
        }

        boolean hasProps = false;

        BlockData blockData = block.getBlockData();
        if (blockData instanceof Stairs stairs) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Stairs-Shape: ", NamedTextColor.YELLOW))
                    .append(Component.text(stairs.getShape().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Bamboo bamboo) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Bamboo-Leaves: ", NamedTextColor.YELLOW))
                    .append(Component.text(bamboo.getLeaves().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Bed bed) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Bed-Part: ", NamedTextColor.YELLOW))
                    .append(Component.text(bed.getPart().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Beehive beehive) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("BeeHive-HoneyLevel: ", NamedTextColor.YELLOW))
                    .append(Component.text(beehive.getHoneyLevel(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Bell bell) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Bell-Attachment: ", NamedTextColor.YELLOW))
                    .append(Component.text(bell.getAttachment().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof BigDripleaf bigDripleaf) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("BigDripleaf-Tilt: ", NamedTextColor.YELLOW))
                    .append(Component.text(bigDripleaf.getTilt().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof BrewingStand brewingStand) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("BrewingStand-Bottles: ", NamedTextColor.YELLOW))
                    .append(Component.text(brewingStand.getBottles().stream().map(Object::toString).collect(Collectors.joining(", ")), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Cake cake) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Cake-Bites: ", NamedTextColor.YELLOW))
                            .append(Component.text(cake.getBites(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Campfire campfire) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Campfire-signalFire: ", NamedTextColor.YELLOW))
                            .append(Component.text(campfire.isSignalFire(), NamedTextColor.RED))
                    .build()
            );
        }


        if (blockData instanceof BubbleColumn bubbleColumn) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("BubbleColumn-Drag: ", NamedTextColor.YELLOW))
                    .append(Component.text(bubbleColumn.isDrag(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Candle candle) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Candle-candles: ", NamedTextColor.YELLOW))
                            .append(Component.text(candle.getCandles(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof CaveVinesPlant caveVinesPlant) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("CaveVinesPlant-Berries: ", NamedTextColor.YELLOW))
                            .append(Component.text(caveVinesPlant.isBerries(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Chest chest) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Chest-type: ", NamedTextColor.YELLOW))
                            .append(Component.text(chest.getType().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof CommandBlock commandBlock) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("CommandBlock-Conditional: ", NamedTextColor.YELLOW))
                            .append(Component.text(commandBlock.isConditional(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Comparator comparator) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Comparator-Mode: ", NamedTextColor.YELLOW))
                            .append(Component.text(comparator.getMode().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof DaylightDetector daylightDetector) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("DayLightDetector-Inverted: ", NamedTextColor.YELLOW))
                            .append(Component.text(daylightDetector.isInverted(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Dispenser dispenser) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Dispenser-Triggered: ", NamedTextColor.YELLOW))
                            .append(Component.text(dispenser.isTriggered(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Door door) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Door-Hinge: ", NamedTextColor.YELLOW))
                            .append(Component.text(door.getHinge().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof EndPortalFrame endPortalFrame) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("EndPortalFrame-Eye: ", NamedTextColor.YELLOW))
                            .append(Component.text(endPortalFrame.hasEye(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Farmland farmland) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Farmland-Moisture: ", NamedTextColor.YELLOW))
                            .append(Component.text(farmland.getMoisture(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Gate gate) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Gate-InWall: ", NamedTextColor.YELLOW))
                            .append(Component.text(gate.isInWall(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Hopper hopper) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Hopper-enabled: ", NamedTextColor.YELLOW))
                            .append(Component.text(hopper.isEnabled(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Jigsaw jigsaw) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Jigsaw-Orientation: ", NamedTextColor.YELLOW))
                            .append(Component.text(jigsaw.getOrientation().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Jukebox jukebox) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Jukebox-HasRecord: ", NamedTextColor.YELLOW))
                            .append(Component.text(jukebox.hasRecord(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Leaves leaves) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Leaves-Persistent: ", NamedTextColor.YELLOW))
                            .append(Component.text(leaves.isPersistent(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Leaves-Distance: ", NamedTextColor.YELLOW))
                            .append(Component.text(leaves.getDistance(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Lectern lectern) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Lectern-HasBook: ", NamedTextColor.YELLOW))
                            .append(Component.text(lectern.hasBook(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof NoteBlock noteBlock) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("NoteBlock-Instrument: ", NamedTextColor.YELLOW))
                            .append(Component.text(noteBlock.getInstrument().name(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("NoteBlock-Note: ", NamedTextColor.YELLOW))
                            .append(Component.text(noteBlock.getNote().getId(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Piston piston) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Piston-Extended: ", NamedTextColor.YELLOW))
                            .append(Component.text(piston.isExtended(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof PistonHead pistonHead) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("PistonHead-isShort: ", NamedTextColor.YELLOW))
                            .append(Component.text(pistonHead.isShort(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof PointedDripstone pointedDripstone) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("PointedDripstone-verticalDirection: ", NamedTextColor.YELLOW))
                            .append(Component.text(pointedDripstone.getVerticalDirection().name(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("PointedDripstone-thickness: ", NamedTextColor.YELLOW))
                            .append(Component.text(pointedDripstone.getThickness().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof RedstoneWire redstoneWire) {
            hasProps = true;
            for (BlockFace face : redstoneWire.getAllowedFaces()) {
                player.sendMessage(Component.text()
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("RedstoneWire-" + face.name() + "-Connection: ", NamedTextColor.YELLOW))
                        .append(Component.text(redstoneWire.getFace(face).name(), NamedTextColor.RED))
                        .build()
                );
            }
        }

        if (blockData instanceof Repeater repeater) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Repeater-Delay: ", NamedTextColor.YELLOW))
                            .append(Component.text(repeater.getDelay(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Repeater-Locked: ", NamedTextColor.YELLOW))
                            .append(Component.text(repeater.isLocked(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof RespawnAnchor respawnAnchor) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("RespawnAnchor-Charges: ", NamedTextColor.YELLOW))
                            .append(Component.text(respawnAnchor.getCharges(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Sapling sapling) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Sapling-stage: ", NamedTextColor.YELLOW))
                            .append(Component.text(sapling.getStage(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Scaffolding scaffolding) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Scaffolding-bottom: ", NamedTextColor.YELLOW))
                            .append(Component.text(scaffolding.isBottom(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Scaffolding-distance: ", NamedTextColor.YELLOW))
                            .append(Component.text(scaffolding.getDistance(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof SculkCatalyst sculkCatalyst) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("SculkCatalyst-bloom: ", NamedTextColor.YELLOW))
                            .append(Component.text(sculkCatalyst.isBloom(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof SculkSensor sculkSensor) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("SculkSensor-Phase: ", NamedTextColor.YELLOW))
                            .append(Component.text(sculkSensor.getPhase().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof SculkShrieker sculkShrieker) {
            hasProps = true;
            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("SculkShrieker-canSummon: ", NamedTextColor.YELLOW))
                            .append(Component.text(sculkShrieker.isCanSummon(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("SculkShrieker-shrieking: ", NamedTextColor.YELLOW))
                            .append(Component.text(sculkShrieker.isShrieking(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof SeaPickle seaPickle) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("SeaPickle-pickles: ", NamedTextColor.YELLOW))
                    .append(Component.text(seaPickle.getPickles(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Slab slab) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Slab-Type: ", NamedTextColor.YELLOW))
                    .append(Component.text(slab.getType().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Snow snow) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Snow-layers: ", NamedTextColor.YELLOW))
                    .append(Component.text(snow.getLayers(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof StructureBlock structureBlock) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("StructureBlock-Mode: ", NamedTextColor.YELLOW))
                    .append(Component.text(structureBlock.getMode().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof TechnicalPiston technicalPiston) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("TechnicalPiston-type: ", NamedTextColor.YELLOW))
                    .append(Component.text(technicalPiston.getType().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof TNT tnt) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("TNT-Unstable: ", NamedTextColor.YELLOW))
                    .append(Component.text(tnt.isUnstable(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Tripwire tripwire) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Tripwire-disarmed: ", NamedTextColor.YELLOW))
                    .append(Component.text(tripwire.isDisarmed(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof TurtleEgg turtleEgg) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("TurtleEgg-eggs: ", NamedTextColor.YELLOW))
                    .append(Component.text(turtleEgg.getEggs(), NamedTextColor.RED))
                    .build()
            );

            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("TurtleEgg-Hatch: ", NamedTextColor.YELLOW))
                    .append(Component.text(turtleEgg.getHatch(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Wall wall) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Wall-Up: ", NamedTextColor.YELLOW))
                    .append(Component.text(wall.isUp(), NamedTextColor.RED))
                    .build()
            );

            for (BlockFace face : WALL_FACES) {
                player.sendMessage(Component.text()
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Wall-" + face.name() + "-Height: ", NamedTextColor.YELLOW))
                        .append(Component.text(wall.getHeight(face).name(), NamedTextColor.RED))
                        .build()
                );
            }
        }

        if (blockData instanceof Ageable ageable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Ageable-age: ", NamedTextColor.YELLOW))
                    .append(Component.text(ageable.getAge(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof AnaloguePowerable analoguePowerable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("AnaloguePowerable-power: ", NamedTextColor.YELLOW))
                    .append(Component.text(analoguePowerable.getPower(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Attachable attachable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Attachable-attached: ", NamedTextColor.YELLOW))
                    .append(Component.text(attachable.isAttached(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Bisected bisected) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Bisected-Half: ", NamedTextColor.YELLOW))
                    .append(Component.text(bisected.getHalf().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Directional directional) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Directional-facing: ", NamedTextColor.YELLOW))
                    .append(Component.text(directional.getFacing().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof FaceAttachable faceAttachable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("FaceAttachable-attachedFace: ", NamedTextColor.YELLOW))
                    .append(Component.text(faceAttachable.getAttachedFace().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Hangable hangable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Hangable-Hanging: ", NamedTextColor.YELLOW))
                    .append(Component.text(hangable.isHanging(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Levelled levelled) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Levelled-Level: ", NamedTextColor.YELLOW))
                    .append(Component.text(levelled.getLevel(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Lightable lightable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Lightable-lit: ", NamedTextColor.YELLOW))
                    .append(Component.text(lightable.isLit(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof MultipleFacing multipleFacing) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("MultipleFacing-faces: ", NamedTextColor.YELLOW))
                    .append(Component.text(
                            multipleFacing.getFaces().stream()
                                    .map(Enum::name)
                                    .collect(Collectors.joining(", ")),
                            NamedTextColor.RED
                    ))
                    .build()
            );
        }

        if (blockData instanceof Openable openable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Openable-open: ", NamedTextColor.YELLOW))
                    .append(Component.text(openable.isOpen(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Orientable orientable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Orientable-axis: ", NamedTextColor.YELLOW))
                    .append(Component.text(orientable.getAxis().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Powerable powerable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Powerable-powered: ", NamedTextColor.YELLOW))
                    .append(Component.text(powerable.isPowered(), powerable.isPowered() ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Rail rail) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Rail-shape: ", NamedTextColor.YELLOW))
                    .append(Component.text(rail.getShape().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Rotatable rotatable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Rotatable-rotation: ", NamedTextColor.YELLOW))
                    .append(Component.text(rotatable.getRotation().name(), NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Snowable snowable) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Snowable-Snowy: ", NamedTextColor.YELLOW))
                    .append(Component.text(snowable.isSnowy(), snowable.isSnowy() ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .build()
            );
        }

        if (blockData instanceof Waterlogged waterlogged) {
            hasProps = true;
            player.sendMessage(Component.text()
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Waterlogged-water: ", NamedTextColor.YELLOW))
                    .append(Component.text(waterlogged.isWaterlogged(), NamedTextColor.RED))
                    .build()
            );
        }

        // Custom head
        if (blockData instanceof Skull skull) {
            hasProps = true;
            if (skull.hasOwner()) {
                PlayerProfile profile = skull.getPlayerProfile();
                // TODO
            } else {
                player.sendMessage(Component.text()
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("Skull-owner: ", NamedTextColor.YELLOW))
                        .append(Component.text("none", NamedTextColor.RED))
                        .build()
                );
            }
        }

        if (!hasProps) {
            player.sendMessage(Component.text("There isn't an additional props for this block", NamedTextColor.RED, TextDecoration.BOLD));
            player.sendMessage(Component.text("Class: ", NamedTextColor.DARK_GRAY).append(Component.text(blockData.getClass().getName(), NamedTextColor.RED)));
        }

        player.sendMessage(Component.text("=".repeat(48), NamedTextColor.DARK_GRAY));
    }

}
