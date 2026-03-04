package net.apartium.cocoabeans.spigot.schematic;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.schematic.PasteResult;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import net.apartium.cocoabeans.schematic.format.BlockChunkIndexEncoder;
import net.apartium.cocoabeans.schematic.format.CocoaSchematicFormat;
import net.apartium.cocoabeans.schematic.format.SimpleBlockDataEncoder;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BooleanPropFormat;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.spigot.Locations;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import net.apartium.cocoabeans.spigot.schematic.prop.DirectionalFaceProp;
import net.apartium.cocoabeans.spigot.schematic.prop.OpenableOpenProp;
import net.apartium.cocoabeans.spigot.schematic.prop.PowerablePoweredProp;
import net.apartium.cocoabeans.spigot.schematic.prop.WaterloggedProp;
import net.apartium.cocoabeans.spigot.schematic.prop.format.BlockFacePropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.format.OrientableAxisPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.format.SlabTypePropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.format.StairsPropFormat;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CodeSnippetsTest extends SpigotTestBase {

    Player getPlayer() {
        PlayerMock player = server.addPlayer("ikfir");
        player.teleport(new Location(player.getWorld(), 0, 64, 0));
        return player;
    }

    @Test
    void quickStart() {
        Player player = getPlayer();
        Location location = player.getLocation();

        Position pos1 = Locations.toPosition(location.clone().add(-5, -5, -5));
        Position pos2 = Locations.toPosition(location.clone().add(5, 5, 5));

        // Load a schematic from a cube around the player
        SpigotSchematic schematic = SpigotSchematicHelper.load(
                "test-schematic",
                player.getName(),
                Locations.toPosition(location),
                location.getWorld(),
                pos1,
                pos2,
                SpigotSchematicPlacer.getInstance()
        );
        assertNotNull(schematic);

        // move the player somewhere else
        player.teleport(new Location(location.getWorld(), 0, 100, 0));

        // Create a paste operation
        // SpigotPasteOperation gives flexibility of control to the library user
        SpigotPasteOperation pasteOperation = schematic.paste(location);
        assertNotNull(pasteOperation);

        // just paste all the blocks in one go
        pasteOperation.performAll();
    }

    private void buildHouseInWorld(World world) {
        // floor
        for (int x = 0; x <= 2; x++)
            for (int z = 0; z <= 2; z++)
                world.getBlockAt(x, 64, z).setType(Material.STONE);

        // walls – perimeter only at y=65
        for (int x = 0; x <= 2; x++) {
            world.getBlockAt(x, 65, 0).setType(Material.OAK_PLANKS);
            world.getBlockAt(x, 65, 2).setType(Material.OAK_PLANKS);
        }
        world.getBlockAt(0, 65, 1).setType(Material.OAK_PLANKS);
        world.getBlockAt(2, 65, 1).setType(Material.OAK_PLANKS);
    }


    @Test
    void captureFromWorld() {
        Player player = getPlayer();
        buildHouseInWorld(player.getWorld());

        Position pos0 = Locations.toPosition(player.getLocation());
        Position pos1 = Locations.toPosition(player.getLocation().clone().add(3, 2, 3));

        SpigotSchematic schematic = SpigotSchematicHelper.load(
                "my-house", // title
                player.getName(), // author
                Locations.toPosition(player.getLocation()).floor(), // paste origin offset
                player.getWorld(),
                pos0,
                pos1,
                SpigotSchematicPlacer.getInstance()
        );

        if (schematic == null) {
            player.sendMessage("Failed to capture schematic!");
            fail("Failed to capture schematic");
            return;
        }

        player.sendMessage("Captured " + schematic.metadata().title() + "!");
        assertEquals(new NamespacedKey("minecraft", "stone"), schematic.getBlockData(0, 0, 0).type());
    }

    @Test
    void createSchematicFromScratch() {
        BlockData stone = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
        BlockData planks = new GenericBlockData(new NamespacedKey("minecraft", "oak_planks"), Map.of());

        SpigotSchematic schematic = new SpigotSchematicBuilder()
                .metadata(meta -> meta
                        .title("My Structure")
                        .author("PlayerName")
                        .build())
                .size(new AreaSize(3, 2, 3))
                // floor
                .setBlock(0, 0, 0, stone).setBlock(1, 0, 0, stone).setBlock(2, 0, 0, stone)
                .setBlock(0, 0, 1, stone).setBlock(1, 0, 1, stone).setBlock(2, 0, 1, stone)
                .setBlock(0, 0, 2, stone).setBlock(1, 0, 2, stone).setBlock(2, 0, 2, stone)
                // walls (perimeter of y=1)
                .setBlock(0, 1, 0, planks).setBlock(1, 1, 0, planks).setBlock(2, 1, 0, planks)
                .setBlock(0, 1, 1, planks).setBlock(2, 1, 1, planks)
                .setBlock(0, 1, 2, planks).setBlock(1, 1, 2, planks).setBlock(2, 1, 2, planks)
                .build();

        assertNotNull(schematic);

        assertEquals(stone, schematic.getBlockData(0, 0, 0));
        assertEquals(planks, schematic.getBlockData(0, 1, 2));
    }

    CocoaSchematicFormat<SpigotSchematic> setupFormat() {
        CocoaSchematicFormat<SpigotSchematic> format = new CocoaSchematicFormat<>(
                Map.of(
                        SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of(
                                // Register prop formats for the block properties you want to persist.
                                // For basic blocks (stone, planks, etc.) this can be left empty:
                                // Map.of()
                                BlockProp.WATERLOGGED, new BooleanPropFormat(WaterloggedProp::new),
                                BlockProp.DIRECTIONAL, new BlockFacePropFormat(DirectionalFaceProp::new),
                                BlockProp.ORIENTABLE_AXIS, OrientableAxisPropFormat.INSTANCE,
                                BlockProp.OPENABLE_OPEN, new BooleanPropFormat(OpenableOpenProp::new),
                                BlockProp.POWERABLE_POWERED, new BooleanPropFormat(PowerablePoweredProp::new),
                                BlockProp.SLAB_TYPE, SlabTypePropFormat.INSTANCE,
                                BlockProp.STAIRS_SHAPE, StairsPropFormat.INSTANCE
                                // Add more as needed...
                        ))
                ),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.raw(), CompressionEngine.gzip()),
                CompressionType.GZIP.getId(),   // compression for block types
                CompressionType.GZIP.getId(),   // compression for block index
                new SpigotSchematicFactory()
        );

        return format;
    }

    Path getSchematicDirectory() throws IOException {
        Path dataDir = plugin.getDataFolder().toPath();
        Path schematics = dataDir.resolve("schematics");
        Files.createDirectories(schematics);
        return schematics;
    }

    private SpigotSchematic createSchematic() {
        return new SpigotSchematicBuilder()
                .metadata(metadata -> metadata
                        .title("my-schematic")
                        .build()
                )
                .build();
    }

    @Test
    void saveToFile() throws IOException {
        CocoaSchematicFormat<SpigotSchematic> format = setupFormat();
        SpigotSchematic schematic = createSchematic();

        Path myFile = getSchematicDirectory().resolve("my-schematic.schem");
        try (SeekableOutputStream out = SeekableOutputStream.open(myFile)) {
            format.write(schematic, out);
        }

        assertTrue(Files.exists(myFile));
    }

    @Test
    void loadFromFile() throws IOException {
        saveToFile();
        CocoaSchematicFormat<SpigotSchematic> format = setupFormat();

        Path myFile = getSchematicDirectory().resolve("my-schematic.schem");
        SpigotSchematic schematic;
        try (SeekableInputStream in = SeekableInputStream.open(myFile)) {
            schematic = format.read(in);
        }

        if (schematic == null)
            fail("Failed to load schematic");

        assertEquals("my-schematic", schematic.metadata().title());
    }

    byte[] saveToBytes() {
        CocoaSchematicFormat<SpigotSchematic> format = setupFormat();
        SpigotSchematic schematic = createSchematic();

        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        format.write(schematic, new SeekableOutputStream(channel));

        return channel.toByteArray();  // hand off to wherever you need them
    }

    @Test
    void loadFromBytes() {
        CocoaSchematicFormat<SpigotSchematic> format = setupFormat();
        byte[] bytes = saveToBytes();

        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(bytes));
        SpigotSchematic schematic = format.read(in);

        assertNotNull(schematic);
        assertEquals("my-schematic", schematic.metadata().title());
    }

    private void assertHouseInWorld(Location offset) {
        World world = offset.getWorld();
        for (int x = 0; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                Block block = world.getBlockAt(
                        offset.getBlockX() + x,
                        offset.getBlockY(),
                        offset.getBlockZ() + z
                );

                assertEquals(Material.STONE, block.getType());
            }

            Block block = world.getBlockAt(
                    offset.getBlockX() + x,
                    offset.getBlockY() + 1,
                    offset.getBlockZ()
            );

            assertEquals(Material.OAK_PLANKS, block.getType());

            block = world.getBlockAt(
                    offset.getBlockX() + x,
                    offset.getBlockY() + 1,
                    offset.getBlockZ() + 2
            );

            assertEquals(Material.OAK_PLANKS, block.getType());
        }


        Block block = world.getBlockAt(
                offset.getBlockX(),
                offset.getBlockY() + 1,
                offset.getBlockZ() + 1
        );

        assertEquals(Material.OAK_PLANKS, block.getType());

        block = world.getBlockAt(
                offset.getBlockX() + 2,
                offset.getBlockY() + 1,
                offset.getBlockZ() + 1
        );

        assertEquals(Material.OAK_PLANKS, block.getType());
    }

    @Test
    void roundTrip() throws IOException {
        // 1. Capture
        Player player = getPlayer();
        buildHouseInWorld(player.getWorld());

        Position origin = Locations.toPosition(player.getLocation()).floor();
        Position pos0 = Locations.toPosition(player.getLocation());
        Position pos1 = Locations.toPosition(player.getLocation().clone().add(3, 2, 3));

        SpigotSchematic captured = SpigotSchematicHelper.load(
                "house", player.getName(),
                origin, player.getWorld(),
                pos0, pos1,
                SpigotSchematicPlacer.getInstance()
        );

        // 2. Save
        CocoaSchematicFormat<SpigotSchematic> format = setupFormat();
        Path myFile = getSchematicDirectory().resolve("my-schematic.schem");
        try (SeekableOutputStream out = SeekableOutputStream.open(myFile)) {
            format.write(captured, out);
        }

        // 3. Reload (e.g. on server restart)
        SpigotSchematic loaded;
        try (SeekableInputStream in = SeekableInputStream.open(myFile)) {
            loaded = format.read(in);
        }

        // 4. Paste
        player.teleport(new Location(player.getWorld(), 0, 100, 0));
        PasteResult result = loaded.paste(player.getLocation()).performAll();
        assertHouseInWorld(player.getLocation());
        assertEquals(17, result.blockPlaces(), "All blocks should be placed");

    }

    @Test
    void basicPasting() {
        Player player = getPlayer();
        SpigotSchematic schematic = createSchematic();

        Location origin = player.getLocation();
        PasteResult result = schematic.paste(origin).performAll();

        player.sendMessage("Placed " + result.blockPlaces() + " blocks.");
    }

    void incrementalPasting() {
        Player player = getPlayer();
        SpigotSchematic schematic = createSchematic();

        SpigotPasteOperation operation = schematic.paste(player.getLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!operation.hasNext()) {
                    this.cancel();
                    player.sendMessage("Pasting finished.");
                    return;
                }

                // Paste up to 100 blocks per tick
                operation.advanceAllAxis(100);

            }
        }.runTaskTimer(plugin, 0, 1);
    }

    void placementFilter() {
        SpigotSchematic schematic = createSchematic();
        Player player = getPlayer();

        Location origin = player.getLocation();

        {
            SpigotPasteOperation operation = schematic.paste(origin);
            operation.setShouldPlace((block, placement) -> true);
            operation.performAll();
        }
        {
            SpigotPasteOperation operation = schematic.paste(origin);
            operation.setShouldPlace((block, placement) -> block.getType() == Material.AIR);
            operation.performAll();
        }
        {
            SpigotPasteOperation operation = schematic.paste(origin);
            operation.setShouldPlace((block, placement) -> {
                Material type = block.getType();
                return type == Material.WATER || type == Material.LAVA;
            });
            operation.performAll();
        }
        {
            SpigotPasteOperation operation = schematic.paste(origin);
            operation.setShouldPlace((block, placement) -> block.getY() < 64);
            operation.performAll();
        }
    }

    void axisOrderControl() {
        SpigotSchematic schematic = createSchematic();
        Player player = getPlayer();

        SpigotPasteOperation operation = schematic.paste(player.getLocation(), AxisOrder.YXZ);
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!operation.hasNext()) {
                    this.cancel();
                    return;
                }

                operation.performAllOnDualAxis();
                // play a sound etc
            }

        }.runTaskTimer(plugin, 0, 1);
    }

    void blockMapping() {
        SpigotSchematic schematic = createSchematic();
        Player player = getPlayer();

        BlockData diamondBlock = new GenericBlockData(
                new NamespacedKey("minecraft", "diamond_block"),
                Map.of()
        );

        SpigotPasteOperation operation = schematic.paste(player.getLocation());
        operation.setMapper(placement -> {
            if (placement.block().type().key().equals("stone"))
                return diamondBlock;

            return placement.block();
        });

        operation.performAll();

    }

    void postplacement() {
        SpigotSchematic schematic = createSchematic();
        Player player = getPlayer();

        Location origin = player.getLocation();

        SpigotPasteOperation operation = schematic.paste(origin);

        // Spawn particles at every placed block
        operation.addPostPlaceAction((block, blockData) -> block.getWorld().spawnParticle(
                Particle.CLOUD,
                block.getLocation().add(0.5, 0.5, 0.5),
                5,
                0.25, 0.25, 0.25,
                0.0
        ));

        // Log each placement
        operation.addPostPlaceAction((block, blockData) ->
                plugin.getLogger().info(
                        "Placed " + blockData.type().key() + " at " + block.getLocation()
                )
        );

        operation.performAll();
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    delete(child);
                }
            }
        }

        file.delete();
    }

    @AfterEach
    void cleanUp() {
        if (plugin.getDataFolder().exists())
            delete(plugin.getDataFolder());
    }

}
