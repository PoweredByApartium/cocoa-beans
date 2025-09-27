package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.*;
import net.apartium.cocoabeans.space.schematic.axis.Axis;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.space.schematic.utils.ByteArraySeekableChannel;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.Entry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

class CocoaSchematicFormatTest {

    @Test
    void soFar() {
        SchematicFormat format = SchematicFormat.COCOA;

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

            @Override
            public UUID id() {
                return UUID.randomUUID();
            }

            @Override
            public Instant created() {
                return Instant.now();
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
                return new Position(0, 0, 0);
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
                return null;
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
            public Schematic rotate(int rotate) {
                return null;
            }

            @Override
            public Schematic flip(Axis axis) {
                return null;
            }

            @Override
            public Schematic translate(Position offset) {
                return null;
            }

            @Override
            public Schematic translate(AxisOrder axisOrder) {
                return null;
            }
        };

        try (ByteArraySeekableChannel channel = new ByteArraySeekableChannel()) {

            SeekableOutputStream out = new SeekableOutputStream(channel);

            format.write(schematic, out);
            System.out.println(Arrays.toString(channel.toByteArray()));
            System.out.println("len: " + channel.size());


            SeekableInputStream in = new SeekableInputStream(channel);

            in.position(0);
            Schematic schem = format.read(in);
            System.out.println(schem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
