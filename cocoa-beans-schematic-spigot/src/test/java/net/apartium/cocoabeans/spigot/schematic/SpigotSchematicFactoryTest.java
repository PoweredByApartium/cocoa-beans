package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertmions.*;

class SpigotSchematicFactoryTest {

    private static BlockData stone() {
        return new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
    }

    private static SpigotSchematic create(SpigotSchematicFactory factory, BlockIterator iter, AreaSize size) {
        return factory.createSchematic(
                Instant.EPOCH,
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "1.0"),
                SchematicMetadata.of(),
                iter,
                size,
                AxisOrder.XYZ,
                Position.ZERO,
                Map.of()
        );
    }

    @Test
    void createSchematic_returnsSpigotSchematicBuilder() {
        SchematicBuilder<SpigotSchematic> builder = new SpigotSchematicFactory().createSchematic();
        assertInstanceOf(SpigotSchematicBuilder.class, builder);
    }

    @Test
    void createSchematic_withNoBlocks_returnsEmptySchematic() {
        SpigotSchematic schematic = create(
                new SpigotSchematicFactory(),
                new BlockChunkIterator(BlockChunk.empty()),
                new AreaSize(0, 0, 0)
        );

        assertNotNull(schematic);
        assertFalse(schematic.blocksIterator().hasNext());
    }

    @Test
    void createSchematic_withBlocks_putsBlocksIntoSchematic() {
        MutableBlockChunkImpl chunk = new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
        chunk.setBlock(new BlockPlacement(new Position(1, 0, 2), stone()));

        SpigotSchematic schematic = create(
                new SpigotSchematicFactory(),
                new BlockChunkIterator(chunk),
                new AreaSize(2, 1, 3)
        );

        assertNotNull(schematic.getBlockData(1, 0, 2));
        assertEquals("stone", schematic.getBlockData(1, 0, 2).type().key());
    }

    @Test
    void createSchematic_preservesCreatedInstant() {
        Instant created = Instant.ofEpochMilli(123456789L);
        SpigotSchematic schematic = new SpigotSchematicFactory().createSchematic(
                created,
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "1.0"),
                SchematicMetadata.of(),
                new BlockChunkIterator(BlockChunk.empty()),
                new AreaSize(0, 0, 0),
                AxisOrder.XYZ,
                Position.ZERO,
                Map.of()
        );

        assertEquals(created, schematic.created());
    }

    @Test
    void createSchematic_preservesPlatform() {
        MinecraftPlatform platform = new MinecraftPlatform(MinecraftVersion.V1_8_9, "spigot", "2.0");
        SpigotSchematic schematic = new SpigotSchematicFactory().createSchematic(
                Instant.EPOCH,
                platform,
                SchematicMetadata.of(),
                new BlockChunkIterator(BlockChunk.empty()),
                new AreaSize(0, 0, 0),
                AxisOrder.XYZ,
                Position.ZERO,
                Map.of()
        );

        assertEquals(platform, schematic.originPlatform());
    }

    @Test
    void createSchematic_preservesOffset() {
        Position offset = new Position(3, 5, 7);
        SpigotSchematic schematic = new SpigotSchematicFactory().createSchematic(
                Instant.EPOCH,
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "1.0"),
                SchematicMetadata.of(),
                new BlockChunkIterator(BlockChunk.empty()),
                new AreaSize(0, 0, 0),
                AxisOrder.XYZ,
                offset,
                Map.of()
        );

        assertEquals(offset, schematic.offset());
    }

    @Test
    void createSchematic_preservesSize() {
        AreaSize size = new AreaSize(4, 2, 8);

        SpigotSchematic schematic = new SpigotSchematicFactory().createSchematic(
                Instant.EPOCH,
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "1.0"),
                SchematicMetadata.of(),
                new BlockChunkIterator(BlockChunk.empty()),
                size,
                AxisOrder.XYZ,
                Position.ZERO,
                Map.of()
        );

        assertEquals(size, schematic.size());
    }

    @Test
    void createSchematic_preservesAxisOrder() {
        SpigotSchematic schematic = new SpigotSchematicFactory().createSchematic(
                Instant.EPOCH,
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "1.0"),
                SchematicMetadata.of(),
                new BlockChunkIterator(BlockChunk.empty()),
                new AreaSize(0, 0, 0),
                AxisOrder.ZYX,
                Position.ZERO,
                Map.of()
        );

        assertEquals(AxisOrder.ZYX, schematic.axisOrder());
    }

    @Test
    void createSchematic_multipleBlocks_allPresent() {
        MutableBlockChunkImpl chunk = new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        chunk.setBlock(new BlockPlacement(new Position(1, 0, 0), stone()));
        chunk.setBlock(new BlockPlacement(new Position(2, 0, 0), stone()));

        SpigotSchematic schematic = create(
                new SpigotSchematicFactory(),
                new BlockChunkIterator(chunk),
                new AreaSize(3, 1, 1)
        );

        assertNotNull(schematic.getBlockData(0, 0, 0));
        assertNotNull(schematic.getBlockData(1, 0, 0));
        assertNotNull(schematic.getBlockData(2, 0, 0));

        int count = 0;
        BlockIterator it = schematic.blocksIterator();
        while (it.hasNext()) { it.next(); count++; }
        assertEquals(3, count);
    }
}
