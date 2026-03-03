package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CocoaSchematicFormatTest {

    final byte[] simpleSchematic = new byte[]
            {
                    67, 66, 83, 67, 0, 1, 0, 0, -66, 0, 2, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0, 0, -1, -1, -1, -5, 0, 4, 0, 0, 0, 5, 0, 0, 0, 5,
                    0, 0, 0, 5, 0, 5, 0, 1, 0, 6, 2, 0, 0, 0, 0, 0, 0, 0, -78, 0, 0, 0, 0, 0, 0, 0, 98, 0, 0, 0, 0, 0, 0, 0, -57, 0, 0, 0,
                    0, 33, 34, -111, -1, 0, 7, 0, 1, 0, 8, 2, 0, 0, 0, 0, 0, 0, 2, 9, 0, 0, 0, 0, 0, 0, 0, 97, 0, 0, 0, 0, 0, 0, 1, 41, 0,
                    0, 0, 0, -79, -43, 13, 15, 0, 9, 0, 0, 1, -102, 106, -121, -96, 91, 0, 10, 0, 0, 0, 4, 107, 102, 105, 114, 0, 11, 0, 0,
                    0, 15, 67, 111, 111, 108, 32, 115, 99, 104, 101, 109, 97, 116, 105, 99, 33, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 9, 117, 110, 105, 116, 45, 116, 101, 115, 116, 0, 0, 0, 5, 48, 46, 48, 46, 49, 31, -117, 8, 0, 0,
                    0, 0, 0, 0, -1, 99, 96, 96, -112, 101, 96, 96, -32, -52, -51, -52, 75, 77, 46, 74, 76, 43, 1, 114, 120, -110, -13, 11,
                    10, 82, -117, -30, -109, 114, -14, -109, -77, -127, 124, 81, 116, 5, 44, -59, -119, 121, 41, 64, 90, 26, 93, -126, 43,
                    -77, 40, 63, 15, -113, -66, -108, -52, -94, 18, -84, -6, -46, -13, 115, 82, -32, -6, 48, 29, -108, -110, -103, -104, -101,
                    -97, -105, -30, 4, 82, 0, 0, -1, -111, 34, 33, -78, 0, 0, 0, 31, -117, 8, 0, 0, 0, 0, 0, 0, -1, 99, 98, -128, 0, 70, 40, 45,
                    8, 37, -123, -95, 124, 71, -88, 116, 35, -108, 94, 9, -91, 47, 66, -23, -97, -1, 63, -50, -4, -8, -1, -93, -96, 32, 3, 42,
                    -120, -124, -46, -118, 56, -24, 72, 26, -45, -44, -78, -121, -26, -18, 21, 16, 96, 96, 0, 97, -126, -22, 25, -95, 16, 2, -118,
                    -48, -36, -121, 70, -61, -29, -61, 10, -121, 58, -104, -71, 48, -13, 38, 2, 0, 15, 13, -43, -79, 9, 2, 0, 0
            };

    final SchematicFormat format = new CocoaSchematicFormat(
            Map.of(
                    SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())
            ),
            Map.of(
                    BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()
            ),
            Set.of(
                    CompressionEngine.gzip(),
                    CompressionEngine.raw()
            ),
            CompressionType.GZIP.getId(),
            CompressionType.GZIP.getId(),
            new TestSchematicFactory()
    );


    @Test
    void soFar() {
        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
        GenericBlockData sandBlock = new GenericBlockData(new NamespacedKey("minecraft", "sand"), Map.of());
        GenericBlockData copperBlock = new GenericBlockData(new NamespacedKey("minecraft", "copper_block"), Map.of());
        GenericBlockData ironBlock = new GenericBlockData(new NamespacedKey("minecraft", "iron_block"), Map.of());
        GenericBlockData goldBlock = new GenericBlockData(new NamespacedKey("minecraft", "gold_block"), Map.of());
        GenericBlockData diamondBlock = new GenericBlockData(new NamespacedKey("minecraft", "diamondBlock"), Map.of());


        Schematic<?> schematic = new Schematic() {

            private final BlockData[][][] blocks = {
                    {
                            { copperBlock, sandBlock, sandBlock, sandBlock, ironBlock, },
                            { dirtBlock, dirtBlock, dirtBlock, dirtBlock, sandBlock, },
                            { sandBlock, dirtBlock, dirtBlock, dirtBlock, sandBlock, },
                            { sandBlock, dirtBlock, dirtBlock, dirtBlock, dirtBlock, },
                            { goldBlock, sandBlock, sandBlock, sandBlock, diamondBlock, }
                    },
                    {
                            { null, null, null, null, null, },
                            { null, dirtBlock, dirtBlock, dirtBlock, null, },
                            { null, dirtBlock, null, dirtBlock, null, },
                            { null, dirtBlock, null, dirtBlock, null, },
                            { null, null, null, null, null, }
                    },
                    {
                            { null, null, null, null, null, },
                            { null, dirtBlock, dirtBlock, dirtBlock, null, },
                            { null, dirtBlock, null, dirtBlock, null, },
                            { null, dirtBlock, null, dirtBlock, null, },
                            { null, null, null, null, null, }
                    },
                    {
                            { null, null, null, null, null, },
                            { null, dirtBlock, dirtBlock, dirtBlock, null, },
                            { null, dirtBlock, dirtBlock, dirtBlock, null, },
                            { null, dirtBlock, dirtBlock, dirtBlock, null, },
                            { null, null, null, null, null, }
                    },
                    {
                            { null, null, null, null, null, },
                            { null, dirtBlock, null, dirtBlock, null, },
                            { null, null, null, null, null, },
                            { null, dirtBlock, null, dirtBlock, null, },
                            { null, null, null, null, null, }
                    },
            };

            private final Instant created = Instant.now();
            private final MinecraftPlatform platform = new MinecraftPlatform(MinecraftVersion.UNKNOWN, "unit-test", "0.0.1");

            @Override
            public @NotNull MinecraftPlatform originPlatform() {
                return platform;
            }

            @Override
            public @NotNull Instant created() {
                return created;
            }

            @Override
            public @NotNull SchematicMetadata metadata() {
                return SchematicMetadata.of();
            }

            @Override
            public @NotNull Position offset() {
                return new Position(5, 0, -5);
            }

            @Override
            public @NotNull AreaSize size() {
                return new AreaSize(5, 5, 5);
            }

            @Override
            public @NotNull AxisOrder axisOrder() {
                return AxisOrder.XYZ;
            }

            @Override
            public Set<BodyExtension<?>> bodyExtensions() {
                return Set.of();
            }

            @Override
            public BlockData getBlockData(int x, int y, int z) {
                return blocks[x][y][z];
            }

            @Override
            public @NotNull BlockIterator blocksIterator() {
                final Iterator<Position> iterator = axisOrder().iterator(
                        Position.ZERO,
                        new Position(
                                size().width() - 1,
                                size().height() - 1,
                                size().depth() - 1
                        ),
                        1
                );

                return new BlockIterator() {

                    private BlockPlacement next;

                    {
                        advance();
                    }

                    private void advance() {
                        next = null;
                        while (iterator.hasNext() && next == null) {
                            Position position = iterator.next();

                            BlockData block = blocks[(int) position.getX()][(int) position.getY()][(int) position.getZ()];
                            if (block != null) {
                                next = new BlockPlacement(position, block);
                            }
                        }
                    }

                    @Override
                    public Position current() {
                        return next.position();
                    }

                    @Override
                    public boolean hasNext() {
                        return next != null;
                    }

                    @Override
                    public BlockPlacement next() {
                        BlockPlacement placement = next;
                        advance();
                        return placement;
                    }
                };
            }

            @Override
            public @NotNull BlockIterator sortedIterator(@NonNull AxisOrder axisOrder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public BlockIterator reverseIterator(AxisOrder axisOrder, Set reverseAxis) {
                return null;
            }


            @Override
            public @NotNull SchematicBuilder<?> toBuilder() {
                throw new UnsupportedOperationException();
            }

        };

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {

            SeekableOutputStream out = new SeekableOutputStream(channel);

            format.write(schematic, out);

            SeekableInputStream in = new SeekableInputStream(channel);

            in.position(0);
            Schematic schem = format.read(in);

            assertEquals(schematic.originPlatform(), schem.originPlatform());
            assertEquals(schematic.created().toEpochMilli(), schem.created().toEpochMilli());
            assertEquals(schematic.metadata().author(), schem.metadata().author());
            assertEquals(schematic.metadata().title(), schem.metadata().title());
            assertEquals(schematic.offset(), schem.offset());
            assertEquals(schematic.size().width(), schem.size().width());
            assertEquals(schematic.size().height(), schem.size().height());
            assertEquals(schematic.size().depth(), schem.size().depth());
            assertEquals(schematic.axisOrder(), schem.axisOrder());

            for (int x = 0; x < schem.size().width(); x++) {
                for (int y = 0; y < schem.size().height(); y++) {
                    for (int z = 0; z < schem.size().depth(); z++) {
                        assertEquals(schematic.getBlockData(x, y, z), schem.getBlockData(x, y, z));
                    }
                }
            }

            BlockIterator blockIterator = schem.blocksIterator();
            assertTrue(blockIterator.hasNext());
            while (blockIterator.hasNext()) {
                BlockPlacement placement = blockIterator.next();
                assertEquals(schematic.getBlockData((int) placement.position().getX(), (int) placement.position().getY(), (int) placement.position().getZ()), placement.block());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void flipAxis() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(simpleSchematic));
        Schematic schematic = format.read(in);

        Schematic otherSchematic = schematic.toBuilder()
                .flip(Axis.X)
                .flip(Axis.Y)
                .flip(Axis.Z)
                .build()
                .toBuilder()
                .flip(Axis.X)
                .flip(Axis.Y)
                .flip(Axis.Z)
                .build();

        BlockIterator blockIterator = otherSchematic.blocksIterator();
        assertTrue(blockIterator.hasNext());
        while (blockIterator.hasNext()) {
            BlockPlacement placement = blockIterator.next();
            assertEquals(schematic.getBlockData((int) placement.position().getX(), (int) placement.position().getY(), (int) placement.position().getZ()), placement.block());
        }

        assertEquals(schematic.offset(), otherSchematic.offset());
    }

    @Test
    void shiftSchematic() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(simpleSchematic));
        Schematic schematic = format.read(in);

        Schematic otherSchematic = schematic.toBuilder()
                .shift(Axis.X, 5)
                .shift(Axis.Y, 3)
                .shift(Axis.Z, 2)
                .build();

        BlockIterator blockIterator = otherSchematic.blocksIterator();
        assertTrue(blockIterator.hasNext());
        while (blockIterator.hasNext()) {
            BlockPlacement placement = blockIterator.next();
            assertEquals(schematic.getBlockData((int) placement.position().getX() - 5, (int) placement.position().getY() - 3, (int) placement.position().getZ() - 2), placement.block());
        }

        assertEquals(
                new AreaSize(
                        schematic.size().width() + 5,
                        schematic.size().height() + 3,
                        schematic.size().depth() + 2
                ),
                otherSchematic.size()
        );
    }

    @Test
    void removeBlock() {
        SchematicBuilder<?> builder = new TestSchematic(
                new MinecraftPlatform(
                        MinecraftVersion.V1_8_9, "test", "0.0.1"
                ),
                Instant.now(),
                SchematicMetadata.of(),
                Position.ZERO,
                AreaSize.box(1),
                AxisOrder.XYZ,
                new BlockChunkIterator(BlockChunk.empty())
        ).toBuilder();

        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 5; z++) {
                    builder = builder.setBlock(x, y, z, dirtBlock);
                }
            }
        }

        Schematic schematic = builder.build();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 5; z++) {
                    assertEquals(dirtBlock, schematic.getBlockData(x, y, z));
                }
            }
        }

        assertEquals(AreaSize.box(5), schematic.size());

        builder = schematic.toBuilder();

        builder = builder.removeBlock(0, 3, 2);

        schematic = builder.build();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 5; z++) {
                    if (x == 0 && y == 3 && z == 2) {
                        assertNull(schematic.getBlockData(x, y, z));
                    } else {
                        assertEquals(dirtBlock, schematic.getBlockData(x, y, z));
                    }
                }
            }
        }

        assertEquals(AreaSize.box(5), schematic.size());

        schematic.blocksIterator().forEachRemaining(block -> {
            if (block.position().getX() == 0 && block.position().getY() == 3 && block.position().getZ() == 2)
                fail("Block shouldn't exists");
        });

        builder = schematic.toBuilder();

        builder.setBlock(new BlockPlacement(
                new Position(10, 3, 2),
                dirtBlock
        ));

        schematic = builder.build();
        schematic.blocksIterator().forEachRemaining(block -> {
            if (block.position().getX() == 0 && block.position().getY() == 3 && block.position().getZ() == 2)
                fail("Block shouldn't exists");
        });

        assertEquals(dirtBlock, schematic.getBlockData(10, 3, 2));
        assertEquals(new AreaSize(11, 5, 5), schematic.size());

        builder = schematic.toBuilder();

        builder.removeBlock(10, 3, 2);

        schematic = builder.build();
        schematic.blocksIterator().forEachRemaining(block -> {
            Position pos = block.position();
            if (pos.getX() == 0 && pos.getY() == 3 && pos.getZ() == 2)
                fail("Block shouldn't exists");

            if (pos.getX() == 10)
                fail("Block shouldn't exists");
        });

        assertNull(schematic.getBlockData(10, 3, 2));
        assertEquals(new AreaSize(5, 5, 5), schematic.size());
    }

    record TestBodyExtension(long id, String data) implements BodyExtension<String> {}

    static class TestBodyExtensionFormat implements BodyExtensionFormat<String> {

        private final long id;

        TestBodyExtensionFormat(long id) {
            this.id = id;
        }

        @Override
        public BodyExtension<String> read(InputStream in, long size) throws IOException {
            byte[] bytes = in.readNBytes((int) size);
            return new TestBodyExtension(id, new String(bytes, StandardCharsets.UTF_8));
        }

        @Override
        public byte[] write(BodyExtension<String> extension) {
            return extension.data().getBytes(StandardCharsets.UTF_8);
        }
    }

    private static TestSchematic buildOneBlockSchematic(SchematicMetadata metadata, AxisOrder axisOrder) {
        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
        SchematicBuilder<TestSchematic> builder = new TestSchematic(
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "unit-test", "0.0.1"),
                Instant.ofEpochMilli(1_000_000_000_000L),
                metadata,
                Position.ZERO,
                AreaSize.box(1),
                axisOrder,
                new BlockChunkIterator(BlockChunk.empty())
        ).toBuilder();
        return builder.setBlock(0, 0, 0, dirtBlock).build();
    }

    @Test
    void readStoredSchematicMetadata() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(simpleSchematic));
        Schematic<?> schematic = format.read(in);

        assertEquals("kfir", schematic.metadata().author());
        assertEquals("Cool schematic!", schematic.metadata().title());
        assertEquals("unit-test", schematic.originPlatform().platformName());
        assertEquals("0.0.1", schematic.originPlatform().platformVersion());
        assertEquals(new AreaSize(5, 5, 5), schematic.size());
        assertEquals(new Position(5, 0, -5), schematic.offset());
        assertEquals(AxisOrder.XYZ, schematic.axisOrder());
        assertNotNull(schematic.created());
    }

    @Test
    void writeAndReadWithAuthorAndTitle() {
        SchematicMetadata metadata = SchematicMetadata.of(Map.of("author", "Test Author", "title", "Test Title"));
        Schematic<?> source = buildOneBlockSchematic(metadata, AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = format.read(in);

            assertEquals("Test Author", result.metadata().author());
            assertEquals("Test Title", result.metadata().title());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void writeAndReadWithAuthorOnly() {
        SchematicMetadata metadata = SchematicMetadata.of(Map.of("author", "Solo Author"));
        Schematic<?> source = buildOneBlockSchematic(metadata, AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = format.read(in);

            assertEquals("Solo Author", result.metadata().author());
            assertNull(result.metadata().title());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void writeAndReadWithTitleOnly() {
        SchematicMetadata metadata = SchematicMetadata.of(Map.of("title", "Solo Title"));
        Schematic<?> source = buildOneBlockSchematic(metadata, AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = format.read(in);

            assertNull(result.metadata().author());
            assertEquals("Solo Title", result.metadata().title());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidFingerprint() {
        byte[] corrupted = Arrays.copyOf(simpleSchematic, simpleSchematic.length);
        corrupted[0] = 'X';

        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
        assertThrows(UncheckedIOException.class, () -> format.read(in));
    }

    @Test
    void invalidVersion() {
        byte[] corrupted = Arrays.copyOf(simpleSchematic, simpleSchematic.length);
        corrupted[4] = 0;
        corrupted[5] = 99;

        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
        assertThrows(UncheckedIOException.class, () -> format.read(in));
    }

    @Test
    void rawCompressionRoundTrip() {
        CocoaSchematicFormat<TestSchematic> rawFormat = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.raw()),
                CompressionType.RAW.getId(),
                CompressionType.RAW.getId(),
                new TestSchematicFactory()
        );

        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            rawFormat.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = rawFormat.read(in);

            assertEquals(source.size(), result.size());
            assertEquals(source.offset(), result.offset());
            assertEquals(source.axisOrder(), result.axisOrder());
            assertEquals(dirtBlock, result.getBlockData(0, 0, 0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void differentAxisOrderRoundTrip() {
        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
        Schematic<?> source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.ZYX);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = format.read(in);

            assertEquals(AxisOrder.ZYX, result.axisOrder());
            assertEquals(source.size(), result.size());
            assertEquals(dirtBlock, result.getBlockData(0, 0, 0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerBlockEncoder() {
        CocoaSchematicFormat<TestSchematic> readFormat = new CocoaSchematicFormat<>(
                new HashMap<>(),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        readFormat.registerBlockEncoder(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of()));

        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(simpleSchematic));
        Schematic<?> result = readFormat.read(in);

        assertNotNull(result);
        assertEquals(new AreaSize(5, 5, 5), result.size());
        assertEquals(AxisOrder.XYZ, result.axisOrder());
    }

    @Test
    void registerIndexEncoder() {
        CocoaSchematicFormat<TestSchematic> readFormat = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                new HashMap<>(),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        readFormat.registerIndexEncoder(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder());

        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(simpleSchematic));
        Schematic<?> result = readFormat.read(in);

        assertNotNull(result);
        assertEquals(new AreaSize(5, 5, 5), result.size());
    }

    @Test
    void registerCompressionEngine() {
        Schematic<?> source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));

            CocoaSchematicFormat<TestSchematic> readFormat = new CocoaSchematicFormat<>(
                    Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                    Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                    Set.of(CompressionEngine.raw()),
                    CompressionType.RAW.getId(),
                    CompressionType.RAW.getId(),
                    new TestSchematicFactory()
            );
            readFormat.registerCompressionEngine(CompressionType.GZIP.getId(), CompressionEngine.gzip());

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = readFormat.read(in);

            assertNotNull(result);
            assertEquals(source.size(), result.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setPreferBlockEncoder() {
        int customId = 42;
        CocoaSchematicFormat<TestSchematic> writeFormat = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        writeFormat.setPreferBlockEncoder(customId, new SimpleBlockDataEncoder(Map.of()));

        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            writeFormat.write(source, new SeekableOutputStream(channel));

            CocoaSchematicFormat<TestSchematic> readFormat = new CocoaSchematicFormat<>(
                    Map.of(customId, new SimpleBlockDataEncoder(Map.of())),
                    Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                    Set.of(CompressionEngine.gzip()),
                    CompressionType.GZIP.getId(),
                    CompressionType.GZIP.getId(),
                    new TestSchematicFactory()
            );

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = readFormat.read(in);

            assertNotNull(result);
            assertEquals(source.size(), result.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setPreferIndexEncoder() {
        int customId = 99;
        CocoaSchematicFormat<TestSchematic> writeFormat = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        writeFormat.setPreferIndexEncoder(customId, new BlockChunkIndexEncoder());

        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            writeFormat.write(source, new SeekableOutputStream(channel));

            CocoaSchematicFormat<TestSchematic> readFormat = new CocoaSchematicFormat<>(
                    Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                    Map.of(customId, new BlockChunkIndexEncoder()),
                    Set.of(CompressionEngine.gzip()),
                    CompressionType.GZIP.getId(),
                    CompressionType.GZIP.getId(),
                    new TestSchematicFactory()
            );

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = readFormat.read(in);

            assertNotNull(result);
            assertEquals(source.size(), result.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void writeAndReadBodyExtension() {
        long extensionId = 42L;
        TestBodyExtension extension = new TestBodyExtension(extensionId, "hello body extension");

        CocoaSchematicFormat<TestSchematic> formatWithExtension = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        formatWithExtension.registerBodyExtensionFormat(extensionId, new TestBodyExtensionFormat(extensionId));

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(extensionId, extension)
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            formatWithExtension.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            TestSchematic result = (TestSchematic) formatWithExtension.read(in);

            Set<BodyExtension<?>> resultExtensions = result.bodyExtensions();
            assertEquals(1, resultExtensions.size());

            BodyExtension<?> resultExtension = resultExtensions.iterator().next();
            assertEquals(extensionId, resultExtension.id());
            assertEquals("hello body extension", resultExtension.data());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void writeAndReadMultipleBodyExtensions() {
        long idA = 10L;
        long idB = 20L;
        TestBodyExtension extensionA = new TestBodyExtension(idA, "alpha");
        TestBodyExtension extensionB = new TestBodyExtension(idB, "beta");

        CocoaSchematicFormat<TestSchematic> formatWithExtension = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        formatWithExtension.registerAllBodyExtensionsFormat(Map.of(
                idA, new TestBodyExtensionFormat(idA),
                idB, new TestBodyExtensionFormat(idB)
        ));

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(idA, extensionA, idB, extensionB)
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            formatWithExtension.write(source, new SeekableOutputStream(channel));

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            TestSchematic result = (TestSchematic) formatWithExtension.read(in);

            Set<BodyExtension<?>> resultExtensions = result.bodyExtensions();
            assertEquals(2, resultExtensions.size());

            Map<Long, Object> dataById = new HashMap<>();
            for (BodyExtension<?> ext : resultExtensions)
                dataById.put(ext.id(), ext.data());

            assertEquals("alpha", dataById.get(idA));
            assertEquals("beta", dataById.get(idB));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void unknownBodyExtensionThrowsOnRead() {
        long extensionId = 99L;
        TestBodyExtension extension = new TestBodyExtension(extensionId, "payload");

        CocoaSchematicFormat<TestSchematic> writeFormat = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        writeFormat.registerBodyExtensionFormat(extensionId, new TestBodyExtensionFormat(extensionId));

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(extensionId, extension)
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            writeFormat.write(source, new SeekableOutputStream(channel));

            // Read format has no body extension format registered
            CocoaSchematicFormat<TestSchematic> readFormat = new CocoaSchematicFormat<>(
                    Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                    Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                    Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                    CompressionType.GZIP.getId(),
                    CompressionType.GZIP.getId(),
                    new TestSchematicFactory()
            );

            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            assertThrows(IllegalArgumentException.class, () -> readFormat.read(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Builds a minimal CBSC byte array whose header section is exactly {@code headerBytes}. */
    private static byte[] buildSchematicWithCustomHeaders(byte[] headerBytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write('C'); baos.write('B'); baos.write('S'); baos.write('C');
        baos.write(0); baos.write(1); // version = 1
        baos.write((headerBytes.length >> 16) & 0xFF);
        baos.write((headerBytes.length >> 8) & 0xFF);
        baos.write(headerBytes.length & 0xFF);
        try { baos.write(headerBytes); } catch (IOException e) { throw new RuntimeException(e); }
        return baos.toByteArray();
    }

    private static byte[] concat(byte[]... parts) {
        int total = 0;
        for (byte[] p : parts) total += p.length;
        byte[] result = new byte[total];
        int pos = 0;
        for (byte[] p : parts) { System.arraycopy(p, 0, result, pos, p.length); pos += p.length; }
        return result;
    }

    /** Overwrites 8 bytes at {@code pos} with 0xFF – enough to corrupt any U64 field. */
    private static byte[] corruptBytes(byte[] data, int pos) {
        byte[] corrupted = Arrays.copyOf(data, data.length);
        Arrays.fill(corrupted, pos, pos + 8, (byte) 0xFF);
        return corrupted;
    }

    // Pre-built header sections (each parseable by parseHeaders)
    // HDR_UNKNOWN: unknown ID 0x00D0, length=1, data=0x00 → hits the default case safely
    private static final byte[] HDR_UNKNOWN              = {0x00, (byte) 0xD0, 0x00, 0x00, 0x01, 0x00};
    private static final byte[] HDR_AXIS_ORDER           = {0x00, 0x02, 0x00};                      // XYZ
    private static final byte[] HDR_OFFSET               = {0x00, 0x03, 0,0,0,0, 0,0,0,0, 0,0,0,0};
    private static final byte[] HDR_SIZE                 = {0x00, 0x04, 0,0,0,1, 0,0,0,1, 0,0,0,1};
    private static final byte[] HDR_BLOCK_DATA_ENCODING  = {0x00, 0x05, 0x00, 0x01};
    private static final byte[] HDR_BLOCK_INFO           = concat(new byte[]{0x00, 0x06}, new byte[CompressionBlockInfo.SIZE]);
    private static final byte[] HDR_INDEX_ENCODING       = {0x00, 0x07, 0x00, 0x01};
    private static final byte[] HDR_INDEX_INFO           = concat(new byte[]{0x00, 0x08}, new byte[CompressionBlockInfo.SIZE]);
    private static final byte[] HDR_TIMESTAMP            = {0x00, 0x09, 0,0,0,0,0,0,0,0};

    /**
     * Fixed file-byte positions for buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ).
     * Header layout (header-array-relative offsets):
     *   blockInfoIndex = 37 → file pos 9+37 = 46
     *   indexInfoIndex = 76 → file pos 9+76 = 85
     * CompressionBlockInfo structure: [1 compressionType][8 uncompressedSize][8 compressedSize][8 offset][8 checksum]
     */
    private static final int BLOCK_INFO_UNCOMPRESSED_SIZE_POS = 9 + 37 + 1;      // 47
    private static final int BLOCK_INFO_CHECKSUM_POS          = 9 + 37 + 1+8+8+8; // 71
    private static final int INDEX_INFO_UNCOMPRESSED_SIZE_POS = 9 + 76 + 1;      // 86
    private static final int INDEX_INFO_CHECKSUM_POS          = 9 + 76 + 1+8+8+8; // 110
    // For a schematic with one body extension (ID=42L, platform "unit-test"/"0.0.1"):
    //   base header = 159 bytes; BODY_EXTENSION adds 2(id)+8(extId)+33(placeholder)=43 bytes
    //   bodyExtensionOffset = 159+2+8 = 169 → file pos = 9+169 = 178
    private static final int BODY_EXT_UNCOMPRESSED_SIZE_POS  = 9 + 169 + 1;      // 179
    private static final int BODY_EXT_CHECKSUM_POS           = 9 + 169 + 1+8+8+8; // 203

    private CocoaSchematicFormat<TestSchematic> createFormatWithBodyExtension(long id) {
        CocoaSchematicFormat<TestSchematic> fmt = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        fmt.registerBodyExtensionFormat(id, new TestBodyExtensionFormat(id));
        return fmt;
    }

    static class InconsistentBodyExtensionSchematic extends TestSchematic {
        private int bodyExtCallCount = 0;
        private final Set<BodyExtension<?>> bodyExtOnSecondCall;

        InconsistentBodyExtensionSchematic(TestSchematic base, Set<BodyExtension<?>> bodyExtOnSecondCall) {
            super(base.originPlatform(), base.created(), base.metadata(),
                    base.offset(), base.size(), base.axisOrder(), base.blocksIterator());
            this.bodyExtOnSecondCall = bodyExtOnSecondCall;
        }

        @Override
        public Set<BodyExtension<?>> bodyExtensions() {
            bodyExtCallCount++;
            return bodyExtCallCount == 1 ? Set.of() : bodyExtOnSecondCall;
        }
    }

    static class NullBodyExtensionFormat implements BodyExtensionFormat<String> {
        private final long id;
        NullBodyExtensionFormat(long id) { this.id = id; }

        @Override public BodyExtension<String> read(InputStream in, long size) { return null; }
        @Override public byte[] write(BodyExtension<String> extension) { return new byte[0]; }
    }

    @Test
    void setDefaultCompressionEngineForBlocks() {
        CocoaSchematicFormat<TestSchematic> fmt = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        fmt.setDefaultCompressionEngineForBlocks(CompressionEngine.raw());

        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));
            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = fmt.read(in);
            assertEquals(source.size(), result.size());
            assertEquals(source.axisOrder(), result.axisOrder());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setDefaultCompressionEngineForIndexes() {
        CocoaSchematicFormat<TestSchematic> fmt = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        fmt.setDefaultCompressionEngineForIndexes(CompressionEngine.raw());

        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));
            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            Schematic<?> result = fmt.read(in);
            assertEquals(source.size(), result.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setDefaultCompressionEngineForBodyExtensions() {
        long id = 55L;
        CocoaSchematicFormat<TestSchematic> fmt = createFormatWithBodyExtension(id);
        fmt.setDefaultCompressionEngineForBodyExtensions(CompressionEngine.raw());

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(id, new TestBodyExtension(id, "ext-data"))
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));
            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            TestSchematic result = (TestSchematic) fmt.read(in);
            assertEquals(1, result.bodyExtensions().size());
            assertEquals("ext-data", result.bodyExtensions().iterator().next().data());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void writeBodyExtensionUnregisteredFormatThrows() {
        CocoaSchematicFormat<TestSchematic> fmt = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(77L, new TestBodyExtension(77L, "x"))
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            assertThrows(IllegalStateException.class, () -> fmt.write(source, new SeekableOutputStream(channel)));
        }
    }

    @Test
    void writeBodyExtensionOffsetMissingThrows() {
        long id = 88L;
        CocoaSchematicFormat<TestSchematic> fmt = createFormatWithBodyExtension(id);

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        // First call to bodyExtensions() returns empty → no placeholder in header.
        // Second call returns non-empty → writeBodyExtension() can't find the offset.
        InconsistentBodyExtensionSchematic source = new InconsistentBodyExtensionSchematic(
                base, Set.of(new TestBodyExtension(id, "data"))
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            assertThrows(IllegalStateException.class, () -> fmt.write(source, new SeekableOutputStream(channel)));
        }
    }

    @Test
    void blockDataUncompressedSizeMismatch() {
        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));
            byte[] corrupted = corruptBytes(channel.toByteArray(), BLOCK_INFO_UNCOMPRESSED_SIZE_POS);
            SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
            assertThrows(UncheckedIOException.class, () -> format.read(in));
        }
    }

    @Test
    void blockDataChecksumMismatch() {
        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));
            byte[] corrupted = corruptBytes(channel.toByteArray(), BLOCK_INFO_CHECKSUM_POS);
            SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
            assertThrows(UncheckedIOException.class, () -> format.read(in));
        }
    }

    @Test
    void indexDataUncompressedSizeMismatch() {
        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));
            byte[] corrupted = corruptBytes(channel.toByteArray(), INDEX_INFO_UNCOMPRESSED_SIZE_POS);
            SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
            assertThrows(UncheckedIOException.class, () -> format.read(in));
        }
    }

    @Test
    void indexDataChecksumMismatch() {
        TestSchematic source = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            format.write(source, new SeekableOutputStream(channel));
            byte[] corrupted = corruptBytes(channel.toByteArray(), INDEX_INFO_CHECKSUM_POS);
            SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
            assertThrows(UncheckedIOException.class, () -> format.read(in));
        }
    }

    @Test
    void bodyExtensionUncompressedSizeMismatch() {
        long id = 42L;
        CocoaSchematicFormat<TestSchematic> fmt = createFormatWithBodyExtension(id);
        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(id, new TestBodyExtension(id, "hello"))
        );
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));
            byte[] corrupted = corruptBytes(channel.toByteArray(), BODY_EXT_UNCOMPRESSED_SIZE_POS);
            SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
            assertThrows(UncheckedIOException.class, () -> fmt.read(in));
        }
    }

    @Test
    void bodyExtensionChecksumMismatch() {
        long id = 42L;
        CocoaSchematicFormat<TestSchematic> fmt = createFormatWithBodyExtension(id);
        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(id, new TestBodyExtension(id, "hello"))
        );
        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));
            byte[] corrupted = corruptBytes(channel.toByteArray(), BODY_EXT_CHECKSUM_POS);
            SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(corrupted));
            assertThrows(UncheckedIOException.class, () -> fmt.read(in));
        }
    }

    @Test
    void bodyExtensionNullReturnFromFormatIsSkipped() {
        long id = 33L;
        CocoaSchematicFormat<TestSchematic> fmt = new CocoaSchematicFormat<>(
                Map.of(SimpleBlockDataEncoder.ID, new SimpleBlockDataEncoder(Map.of())),
                Map.of(BlockChunkIndexEncoder.ID, new BlockChunkIndexEncoder()),
                Set.of(CompressionEngine.gzip(), CompressionEngine.raw()),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );
        fmt.registerBodyExtensionFormat(id, new NullBodyExtensionFormat(id));

        TestSchematic base = buildOneBlockSchematic(SchematicMetadata.of(), AxisOrder.XYZ);
        TestSchematic source = new TestSchematic(
                base.originPlatform(), base.created(), base.metadata(),
                base.offset(), base.size(), base.axisOrder(),
                base.blocksIterator(), Map.of(id, new TestBodyExtension(id, "ignored"))
        );

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {
            fmt.write(source, new SeekableOutputStream(channel));
            SeekableInputStream in = new SeekableInputStream(channel);
            in.position(0);
            TestSchematic result = (TestSchematic) fmt.read(in);
            assertTrue(result.bodyExtensions().isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void assertBaseHeadersEmpty() {
        SeekableInputStream in = new SeekableInputStream(
                ByteArraySeekableChannel.of(buildSchematicWithCustomHeaders(new byte[0])));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingAxisOrder() {
        SeekableInputStream in = new SeekableInputStream(
                ByteArraySeekableChannel.of(buildSchematicWithCustomHeaders(HDR_UNKNOWN)));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingOffset() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingSize() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingBlockDataEncoding() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_SIZE, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingBlockInfo() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_SIZE, HDR_BLOCK_DATA_ENCODING, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingIndexEncoding() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_SIZE, HDR_BLOCK_DATA_ENCODING, HDR_BLOCK_INFO, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingIndexInfo() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_SIZE, HDR_BLOCK_DATA_ENCODING, HDR_BLOCK_INFO, HDR_INDEX_ENCODING, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingTimestamp() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_SIZE, HDR_BLOCK_DATA_ENCODING, HDR_BLOCK_INFO, HDR_INDEX_ENCODING, HDR_INDEX_INFO, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void assertBaseHeadersMissingPlatform() {
        SeekableInputStream in = new SeekableInputStream(ByteArraySeekableChannel.of(
                buildSchematicWithCustomHeaders(concat(HDR_AXIS_ORDER, HDR_OFFSET, HDR_SIZE, HDR_BLOCK_DATA_ENCODING, HDR_BLOCK_INFO, HDR_INDEX_ENCODING, HDR_INDEX_INFO, HDR_TIMESTAMP, HDR_UNKNOWN))));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void parseHeadersDefaultCase() {
        // HDR_UNKNOWN exercises the default switch case (unknown header ID),
        // then assertBaseHeaders fires because no required headers are present.
        SeekableInputStream in = new SeekableInputStream(
                ByteArraySeekableChannel.of(buildSchematicWithCustomHeaders(HDR_UNKNOWN)));
        assertThrows(IllegalStateException.class, () -> format.read(in));
    }

    @Test
    void splitFromAtoBStartOutOfBounds() {
        // OFFSET header with no value bytes: after reading the 2-byte ID,
        // index == headers.length, so splitFromAToB(headers, 2, 14) fires
        // the "Couldn't start from the end!" check (data.length <= a).
        byte[] headers = {0x00, 0x03};
        SeekableInputStream in = new SeekableInputStream(
                ByteArraySeekableChannel.of(buildSchematicWithCustomHeaders(headers)));
        assertThrows(IndexOutOfBoundsException.class, () -> format.read(in));
    }

    @Test
    void splitFromAtoBEndOutOfBounds() {
        // OFFSET header with only 5 value bytes instead of the required 12:
        // splitFromAToB(headers, 2, 14) fires the "Couldn't reach after the end!" check (data.length < b).
        byte[] headers = {0x00, 0x03, 0, 0, 0, 0, 0};
        SeekableInputStream in = new SeekableInputStream(
                ByteArraySeekableChannel.of(buildSchematicWithCustomHeaders(headers)));
        assertThrows(IndexOutOfBoundsException.class, () -> format.read(in));
    }

    @Test
    void simpleSetBlock() {
        SchematicBuilder<?> builder = new TestSchematic(
                new MinecraftPlatform(
                        MinecraftVersion.V1_8_9, "test", "0.0.1"
                ),
                Instant.now(),
                SchematicMetadata.of(),
                Position.ZERO,
                AreaSize.box(1),
                AxisOrder.XYZ,
                new BlockChunkIterator(BlockChunk.empty())
        ).toBuilder();

        GenericBlockData dirtBlock = new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    builder.setBlock(x, y, z, dirtBlock);
                }
            }
        }

        Schematic schematic = builder.build();

        assertEquals(dirtBlock, schematic.getBlockData(0, 0, 0));
        assertEquals(dirtBlock, schematic.getBlockData(1, 0, 0));
        assertEquals(dirtBlock, schematic.getBlockData(0, 1, 0));
        assertEquals(dirtBlock, schematic.getBlockData(0, 0, 1));
        assertEquals(dirtBlock, schematic.getBlockData(1, 1, 0));
        assertEquals(dirtBlock, schematic.getBlockData(1, 0, 1));
        assertEquals(dirtBlock, schematic.getBlockData(0, 1, 1));
        assertEquals(dirtBlock, schematic.getBlockData(1, 1, 1));

        assertNull(schematic.getBlockData(-1, 0, 0));
        assertNull(schematic.getBlockData(0, -1, 0));
        assertNull(schematic.getBlockData(0, 0, -1));
        assertNull(schematic.getBlockData(-1, -1, 0));
        assertNull(schematic.getBlockData(0, -1, -1));
        assertNull(schematic.getBlockData(-1, -1, -1));

        for (int x = 2; x < 10; x++) {
            for (int y = 2; y < 10; y++) {
                for (int z = 2; z < 10; z++) {
                    assertNull(schematic.getBlockData(x, y, z), "Should be null at (" + x + ", " + y + ", " + z + ")");
                    assertNull(schematic.getBlockData(-x, -y, -z), "Should be null at (" + -x + ", " + -y + ", " + -z + ")");
                }
            }
        }
    }

}
