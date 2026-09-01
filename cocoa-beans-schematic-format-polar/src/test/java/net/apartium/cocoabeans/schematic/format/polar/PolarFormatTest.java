package net.apartium.cocoabeans.schematic.format.polar;

import net.apartium.cocoabeans.schematic.Schematic;
import net.apartium.cocoabeans.schematic.SchematicMetadata;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.block.BlockChunkUtils;
import net.apartium.cocoabeans.schematic.prop.StringBlockProp;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PolarFormatTest {

    final PolarFormat<PolarTestSchematic> format = new PolarFormat<>(new PolarTestSchematicFactory());
    final PolarFormat<PolarTestSchematic> uncompressedFormat = new PolarFormat<>(
            new PolarTestSchematicFactory(), PolarFormat.COMPRESSION_NONE
    );

    @Test
    void singleBlockRoundTrip() throws IOException {
        GenericBlockData stoneBlock = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());

        PolarTestSchematic source = buildSchematic(
                Position.ZERO,
                new AreaSize(1, 1, 1),
                new BlockPlacement(Position.ZERO, stoneBlock)
        );

        PolarTestSchematic result = roundTrip(source);

        assertEquals(new AreaSize(1, 1, 1), result.size());
        assertEquals(stoneBlock, result.getBlockData(0, 0, 0));
    }

    @Test
    void singleBlockRoundTripUncompressed() throws IOException {
        GenericBlockData stoneBlock = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());

        PolarTestSchematic source = buildSchematic(
                Position.ZERO,
                new AreaSize(1, 1, 1),
                new BlockPlacement(Position.ZERO, stoneBlock)
        );

        PolarTestSchematic result = roundTrip(source, uncompressedFormat);

        assertEquals(new AreaSize(1, 1, 1), result.size());
        assertEquals(stoneBlock, result.getBlockData(0, 0, 0));
    }

    @Test
    void multipleBlocksRoundTrip() throws IOException {
        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
        GenericBlockData stoneBlock = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());

        MutableBlockChunk chunk = BlockChunk.empty();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), dirtBlock));
        chunk.setBlock(new BlockPlacement(new Position(1, 0, 0), stoneBlock));
        chunk.setBlock(new BlockPlacement(new Position(0, 1, 0), dirtBlock));
        chunk.setBlock(new BlockPlacement(new Position(1, 1, 0), stoneBlock));

        PolarTestSchematic source = new PolarTestSchematic(
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "0.0.1"),
                Instant.now(),
                SchematicMetadata.of(),
                Position.ZERO,
                new AreaSize(2, 2, 1),
                AxisOrder.XZY,
                new BlockChunkIterator(chunk)
        );

        PolarTestSchematic result = roundTrip(source);

        assertEquals(dirtBlock, result.getBlockData(0, 0, 0));
        assertEquals(stoneBlock, result.getBlockData(1, 0, 0));
        assertEquals(dirtBlock, result.getBlockData(0, 1, 0));
        assertEquals(stoneBlock, result.getBlockData(1, 1, 0));
    }

    @Test
    void blockWithPropertiesRoundTrip() throws IOException {
        GenericBlockData stairsBlock = new GenericBlockData(
                new NamespacedKey("minecraft", "oak_stairs"),
                Map.of(
                        "facing", new StringBlockProp("north"),
                        "half", new StringBlockProp("bottom"),
                        "shape", new StringBlockProp("straight"),
                        "waterlogged", new StringBlockProp("false")
                )
        );

        PolarTestSchematic source = buildSchematic(
                Position.ZERO,
                new AreaSize(1, 1, 1),
                new BlockPlacement(Position.ZERO, stairsBlock)
        );

        PolarTestSchematic result = roundTrip(source);

        BlockData resultBlock = result.getBlockData(0, 0, 0);
        assertNotNull(resultBlock);
        assertEquals("minecraft", resultBlock.type().namespace());
        assertEquals("oak_stairs", resultBlock.type().key());
        assertEquals("north", ((StringBlockProp) resultBlock.props().get("facing")).value());
        assertEquals("bottom", ((StringBlockProp) resultBlock.props().get("half")).value());
        assertEquals("straight", ((StringBlockProp) resultBlock.props().get("shape")).value());
        assertEquals("false", ((StringBlockProp) resultBlock.props().get("waterlogged")).value());
    }

    @Test
    void offsetPreserved() throws IOException {
        GenericBlockData stoneBlock = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());

        PolarTestSchematic source = buildSchematic(
                new Position(100, 64, -200),
                new AreaSize(1, 1, 1),
                new BlockPlacement(Position.ZERO, stoneBlock)
        );

        PolarTestSchematic result = roundTrip(source);

        assertEquals(stoneBlock, result.getBlockData(0, 0, 0));
        assertEquals(new Position(100, 64, -200), result.offset());
    }

    @Test
    void emptySchematicRoundTrip() throws IOException {
        PolarTestSchematic source = new PolarTestSchematic(
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "0.0.1"),
                Instant.now(),
                SchematicMetadata.of(),
                Position.ZERO,
                new AreaSize(0, 0, 0),
                AxisOrder.XZY,
                new BlockChunkIterator(BlockChunk.empty())
        );

        PolarTestSchematic result = roundTrip(source);
        assertNotNull(result);
    }

    @Test
    void multiChunkSchematic() throws IOException {
        GenericBlockData stoneBlock = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());

        // Use an iterator-based approach to avoid MutableBlockChunk scaler limits
        BlockPlacement[] placements = {
                new BlockPlacement(new Position(0, 0, 0), stoneBlock),
                new BlockPlacement(new Position(17, 0, 0), dirtBlock),
                new BlockPlacement(new Position(0, 0, 17), stoneBlock),
        };

        PolarTestSchematic source = buildSchematic(
                Position.ZERO,
                new AreaSize(18, 1, 18),
                placements
        );

        PolarTestSchematic result = roundTrip(source);

        assertEquals(stoneBlock, result.getBlockData(0, 0, 0));
        assertEquals(dirtBlock, result.getBlockData(17, 0, 0));
        assertEquals(stoneBlock, result.getBlockData(0, 0, 17));
    }

    @Test
    void invalidMagicThrows() {
        byte[] badData = new byte[]{0, 0, 0, 0};
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(badData));
        assertThrows(UncheckedIOException.class, () -> format.read(in));
    }

    @Test
    void parseBlockStateSimple() {
        BlockData block = PolarFormat.parseBlockState("minecraft:stone");
        assertNotNull(block);
        assertEquals("minecraft", block.type().namespace());
        assertEquals("stone", block.type().key());
        assertTrue(block.props().isEmpty());
    }

    @Test
    void parseBlockStateWithProperties() {
        BlockData block = PolarFormat.parseBlockState("minecraft:oak_stairs[facing=north,half=bottom]");
        assertNotNull(block);
        assertEquals("oak_stairs", block.type().key());
        assertEquals("north", ((StringBlockProp) block.props().get("facing")).value());
        assertEquals("bottom", ((StringBlockProp) block.props().get("half")).value());
    }

    @Test
    void parseBlockStateAir() {
        assertNull(PolarFormat.parseBlockState("minecraft:air"));
    }

    @Test
    void formatBlockStateSimple() {
        GenericBlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
        assertEquals("minecraft:stone", PolarFormat.formatBlockState(block));
    }

    @Test
    void formatBlockStateWithProperties() {
        GenericBlockData block = new GenericBlockData(
                new NamespacedKey("minecraft", "oak_stairs"),
                Map.of("facing", new StringBlockProp("north"))
        );
        assertEquals("minecraft:oak_stairs[facing=north]", PolarFormat.formatBlockState(block));
    }

    @Test
    void airBlocksNotStored() throws IOException {
        GenericBlockData stoneBlock = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());

        MutableBlockChunk chunk = BlockChunk.empty();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stoneBlock));
        // Position (1,0,0) is air (no block set)
        chunk.setBlock(new BlockPlacement(new Position(2, 0, 0), stoneBlock));

        PolarTestSchematic source = new PolarTestSchematic(
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "0.0.1"),
                Instant.now(),
                SchematicMetadata.of(),
                Position.ZERO,
                new AreaSize(3, 1, 1),
                AxisOrder.XZY,
                new BlockChunkIterator(chunk)
        );

        PolarTestSchematic result = roundTrip(source);

        assertEquals(stoneBlock, result.getBlockData(0, 0, 0));
        assertNull(result.getBlockData(1, 0, 0));
        assertEquals(stoneBlock, result.getBlockData(2, 0, 0));
    }

    // --- helpers ---

    private PolarTestSchematic buildSchematic(Position offset, AreaSize size, BlockPlacement... placements) {
        MutableBlockChunk chunk = BlockChunk.empty();
        for (BlockPlacement placement : placements) {
            chunk = BlockChunkUtils.rescaleChunkIfNeeded(chunk, AxisOrder.XZY, placement.position());
            chunk.setBlock(placement);
        }

        return new PolarTestSchematic(
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "test", "0.0.1"),
                Instant.now(),
                SchematicMetadata.of(),
                offset,
                size,
                AxisOrder.XZY,
                new BlockChunkIterator(chunk)
        );
    }

    private PolarTestSchematic roundTrip(PolarTestSchematic source) throws IOException {
        return roundTrip(source, format);
    }

    private PolarTestSchematic roundTrip(PolarTestSchematic source, PolarFormat<PolarTestSchematic> fmt) throws IOException {
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            return fmt.read(in);
        }
    }

}