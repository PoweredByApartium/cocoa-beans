package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.PasteResult;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import net.apartium.cocoabeans.schematic.format.BlockChunkIndexEncoder;
import net.apartium.cocoabeans.schematic.format.CocoaSchematicFormat;
import net.apartium.cocoabeans.schematic.format.SimpleBlockDataEncoder;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SpigotSchematicE2ETest extends SpigotTestBase {

    private static final int FLOOR_Y = 64;
    private static final int WALLS_Y = 65;

    private static final int EXPECTED_BLOCK_COUNT = 17;

    private final CocoaSchematicFormat<SpigotSchematic> format = new CocoaSchematicFormat<>(
            Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
            Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
            Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
            CompressionType.GZIP.getId(),
            CompressionType.GZIP.getId(),
            new SpigotSchematicFactory()
    );

    @Test
    void loadFromWorldSaveToBytesThenLoadBackAndPaste() {
        World world = server.addSimpleWorld("house-world");

        buildHouseInWorld(world);

        // Step 1: load schematic from the world region
        Position playerPos = new Position(0, FLOOR_Y, 0);
        Position pos0 = new Position(0, FLOOR_Y, 0);
        Position pos1 = new Position(2, WALLS_Y, 2);

        SpigotSchematic original = SpigotSchematicHelper.load(
                "house",
                "test-author",
                playerPos,
                world,
                pos0,
                pos1,
                SpigotSchematicPlacer.getInstance()
        );

        assertNotNull(original, "Schematic should be loaded from world");
        assertEquals("house", original.metadata().title());
        assertEquals("test-author", original.metadata().author());
        assertEquals(EXPECTED_BLOCK_COUNT, countBlocks(original));

        // Step 2: serialize to bytes
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        SeekableOutputStream out = new SeekableOutputStream(channel);
        format.write(original, out);
        byte[] bytes = channel.toByteArray();

        assertNotNull(bytes);
        assertTrue(bytes.length > 0, "Serialized bytes should not be empty");

        // Step 3: deserialize from bytes
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(bytes));
        SpigotSchematic loaded = (SpigotSchematic) format.read(in);

        assertNotNull(loaded, "Deserialized schematic should not be null");
        assertEquals("house", loaded.metadata().title());
        assertEquals("test-author", loaded.metadata().author());
        assertEquals(original.size(), loaded.size());
        assertEquals(original.offset(), loaded.offset());
        assertEquals(original.axisOrder(), loaded.axisOrder());
        assertEquals(EXPECTED_BLOCK_COUNT, countBlocks(loaded));

        // verify floor blocks survived the round-trip
        BlockData floorBlock = loaded.getBlockData(0, 0, 0);
        assertNotNull(floorBlock, "Floor block at (0,0,0) should be present");
        assertEquals(new NamespacedKey("minecraft", "stone"), floorBlock.type());

        // interior wall position (1,1,1) in schematic coords should be absent (was AIR)
        assertNull(loaded.getBlockData(1, 1, 1), "Interior position should be absent (was AIR)");

        // Step 4: paste at a different location in the same world
        Location pasteTarget = new Location(world, 10, FLOOR_Y, 10);
        PasteResult result = loaded.paste(pasteTarget).performAll();

        assertEquals(EXPECTED_BLOCK_COUNT, result.blockPlaces(),
                "All non-air blocks should be placed at the target location");

        assertHouseInWorld(pasteTarget);
    }

    private void buildHouseInWorld(World world) {
        // floor
        for (int x = 0; x <= 2; x++)
            for (int z = 0; z <= 2; z++)
                world.getBlockAt(x, FLOOR_Y, z).setType(Material.STONE);

        // walls – perimeter only at y=65
        for (int x = 0; x <= 2; x++) {
            world.getBlockAt(x, WALLS_Y, 0).setType(Material.OAK_PLANKS);
            world.getBlockAt(x, WALLS_Y, 2).setType(Material.OAK_PLANKS);
        }
        world.getBlockAt(0, WALLS_Y, 1).setType(Material.OAK_PLANKS);
        world.getBlockAt(2, WALLS_Y, 1).setType(Material.OAK_PLANKS);
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

    private int countBlocks(SpigotSchematic schematic) {
        int count = 0;
        BlockIterator it = schematic.blocksIterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

}
