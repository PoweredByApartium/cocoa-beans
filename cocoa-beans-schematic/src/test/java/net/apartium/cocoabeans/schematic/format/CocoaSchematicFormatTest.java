package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.axis.Axis;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.utils.ByteArraySeekableChannel;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.Entry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CocoaSchematicFormatTest {

    @Test
    void soFar() {
        SchematicFormat format = new CocoaSchematicFormat(
                Map.of(
                        SimpleBlockDataEncoder.id, new SimpleBlockDataEncoder(Map.of())
                ),
                Set.of(
                        CompressionEngine.gzip(),
                        CompressionEngine.raw()
                ),
                CompressionType.GZIP.getId(),
                CompressionType.GZIP.getId(),
                new TestSchematicFactory()
        );

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

            @Override
            public UUID id() {
                return id;
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
            public Iterator<Entry<Position, BlockData>> blocksIterator() {
                final Iterator<Position> iterator = axisOrder().iterator(
                        Position.ZERO,
                        new Position(
                                size().width() - 1,
                                size().height() - 1,
                                size().depth() - 1
                        ),
                        1
                );

                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<Position, BlockData> next() {
                        Position position = iterator.next();

                        return new Entry<>(position, blocks[(int) position.getX()][(int) position.getY()][(int) position.getZ()]);
                    }
                };
            }

            @Override
            public SchematicBuilder builder() {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
