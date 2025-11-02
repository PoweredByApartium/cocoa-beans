package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Dimensions;
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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CocoaSchematicFormatTest {

    final byte[] simpleSchematic = new byte[]
            {
                    67, 66, 83, 67, 0, 1, 0, 0, -48, 0, 1, -5, -36, 21, 82, -124, 77, 71, 88, -123, -100, 98, 4, -60, -105, 19, 1, 0, 2, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0, 0,
                    -1, -1, -1, -5, 0, 4, 0, 0, 0, 5, 0, 0, 0, 5, 0, 0, 0, 5, 0, 5, 0, 1, 0, 6, 2, 0, 0, 0, 0, 0, 0, 0, -78, 0, 0, 0, 0, 0, 0, 0, 98, 0, 0, 0, 0, 0, 0, 0,
                    -39, 0, 0, 0, 0, 33, 34, -111, -1, 0, 7, 0, 1, 0, 8, 2, 0, 0, 0, 0, 0, 0, 2, 9, 0, 0, 0, 0, 0, 0, 0, 97, 0, 0, 0, 0, 0, 0, 1, 59, 0, 0, 0, 0, -79, -43,
                    13, 15, 0, 9, 0, 0, 1, -102, 67, -75, -96, 24, 0, 10, 0, 0, 0, 4, 107, 102, 105, 114, 0, 11, 0, 0, 0, 15, 67, 111, 111, 108, 32, 115, 99, 104, 101, 109,
                    97, 116, 105, 99, 33, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 117, 110, 105, 116, 45, 116, 101, 115, 116, 0, 0, 0, 5, 48, 46,
                    48, 46, 49, 31, -117, 8, 0, 0, 0, 0, 0, 0, -1, 99, 96, 96, -112, 101, 96, 96, -32, -52, -51, -52, 75, 77, 46, 74, 76, 43, 1, 114, 120, -110, -13, 11, 10,
                    82, -117, -30, -109, 114, -14, -109, -77, -127, 124, 81, 116, 5, 44, -59, -119, 121, 41, 64, 90, 26, 93, -126, 43, -77, 40, 63, 15, -113, -66, -108, -52,
                    -94, 18, -84, -6, -46, -13, 115, 82, -32, -6, 48, 29, -108, -110, -103, -104, -101, -97, -105, -30, 4, 82, 0, 0, -1, -111, 34, 33, -78, 0, 0, 0, 31, -117,
                    8, 0, 0, 0, 0, 0, 0, -1, 99, 98, -128, 0, 70, 40, 45, 8, 37, -123, -95, 124, 71, -88, 116, 35, -108, 94, 9, -91, 47, 66, -23, -97, -1, 63, -50, -4, -8, -1,
                    -93, -96, 32, 3, 42, -120, -124, -46, -118, 56, -24, 72, 26, -45, -44, -78, -121, -26, -18, 21, 16, 96, 96, 0, 97, -126, -22, 25, -95, 16, 2, -118, -48, -36,
                    -121, 70, -61, -29, -61, 10, -121, 58, -104, -71, 48, -13, 38, 2, 0, 15, 13, -43, -79, 9, 2, 0, 0
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

        Schematic schematic = new Schematic() {

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

            private final UUID id = UUID.randomUUID();
            private final Instant created = Instant.now();
            private final MinecraftPlatform platform = new MinecraftPlatform(MinecraftVersion.UNKNOWN, "unit-test", "0.0.1");

            @Override
            public UUID id() {
                return id;
            }

            @Override
            public MinecraftPlatform platform() {
                return platform;
            }

            @Override
            public Instant created() {
                return created;
            }

            @Override
            public String author() {
                return "kfir";
            }

            @Override
            public String title() {
                return "Cool schematic!";
            }

            @Override
            public Position offset() {
                return new Position(5, 0, -5);
            }

            @Override
            public Dimensions size() {
                return new Dimensions(5, 5, 5);
            }

            @Override
            public AxisOrder axisOrder() {
                return AxisOrder.XYZ;
            }

            @Override
            public BlockData getBlockData(int x, int y, int z) {
                return blocks[x][y][z];
            }

            @Override
            public BlockIterator blocksIterator() {
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
            public BlockIterator sortedIterator(AxisOrder axisOrder) {
                return null;
            }


            @Override
            public SchematicBuilder toBuilder() {
                return null;
            }

        };

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {

            SeekableOutputStream out = new SeekableOutputStream(channel);

            format.write(schematic, out);

            SeekableInputStream in = new SeekableInputStream(channel);

            in.position(0);
            Schematic schem = format.read(in);

            assertEquals(schematic.id(), schem.id());
            assertEquals(schematic.platform(), schem.platform());
            assertEquals(schematic.created().toEpochMilli(), schem.created().toEpochMilli());
            assertEquals(schematic.author(), schem.author());
            assertEquals(schematic.title(), schem.title());
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

}
