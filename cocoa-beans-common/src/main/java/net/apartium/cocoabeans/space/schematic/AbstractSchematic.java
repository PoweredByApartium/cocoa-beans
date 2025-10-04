package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.axis.Axis;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.Entry;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class AbstractSchematic implements Schematic {

    protected UUID id;
    protected Instant created;
    protected String author;
    protected String title;

    protected Position offset;
    protected Dimensions size;
    protected AxisOrder axes;

    // TODO may change it for storage optimize
    protected BlockData[][][] blocks;

    public AbstractSchematic() { }

    protected AbstractSchematic(AbstractSchematic that, Dimensions size) {
        this.id = that.id;
        this.created = that.created;
        this.author = that.author;
        this.title = that.title;

        this.offset = new Position(that.offset).floor();
        this.size = new Dimensions(size).floor();
        this.axes = that.axes;

        this.blocks = new BlockData[(int) axes.getFirst().getAlong(size)][(int) axes.getSecond().getAlong(size)][(int) axes.getThird().getAlong(size)];

        for (int i0 = 0; i0 < Math.min(that.blocks.length, this.blocks.length); i0++) {
            this.blocks[i0] = new BlockData[that.blocks[i0].length][];
            for (int i1 = 0; i1 < Math.min(that.blocks[i0].length, this.blocks[i0].length); i1++) {
                this.blocks[i0][i1] = new BlockData[that.blocks[i0][i1].length];
                System.arraycopy(that.blocks[i0][i1], 0, this.blocks[i0][i1], 0, Math.min(that.blocks[i0][i1].length, this.blocks[i0][i1].length));
            }
        }
    }

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
        return author;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public Position offset() {
        return offset;
    }

    @Override
    public Dimensions size() {
        return size;
    }

    @Override
    public AxisOrder axisOrder() {
        return axes;
    }

    @Override
    public BlockData getBlockData(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0)
            return null;

        if (x >= size.width() ||  y >= size.height() || z >= size.depth())
            return null;

        Position pos = new Position(x, y, z);

        return blocks[(int) axes.getFirst().getAlong(pos)][(int) axes.getSecond().getAlong(pos)][(int) axes.getThird().getAlong(pos)];
    }

    @Override
    public Iterator<Entry<Position, BlockData>> blocksIterator() {
        List<Entry<Position, BlockData>> entries = new ArrayList<>();
        for (int i0 = 0; i0 < blocks.length; i0++) {
            for (int i1 = 0; i1 < blocks[0].length; i1++) {
                for (int i2 = 0; i2 < blocks[0][0].length; i2++) {
                    entries.add(new Entry<>(axes.position(i0, i1, i2), blocks[i0][i1][i2]));
                }
            }
        }

        return entries.iterator();
    }

    @Override
    public Schematic rotate(int rotate) {
        return null;  // TODO
    }

    @Override
    public Schematic flip(Axis axis) {
        AbstractSchematic that = new AbstractSchematic(this, this.size);

        if (axis == axes.getFirst()) {
            for (int i0 = 0; i0 < this.blocks.length / 2; i0++) {
                that.blocks[i0] = this.blocks[this.blocks.length - i0 - 1];
                that.blocks[this.blocks.length - i0 - 1] = this.blocks[i0];
            }

            return that;
        }

        if (axis == axes.getSecond()) {
            for (int i0 = 0; i0 < this.blocks.length; i0++) {
                for (int i1 = 0; i1 < this.blocks[i0].length / 2; i1++) {
                    that.blocks[i0][i1] = this.blocks[i0][this.blocks[i0].length - i1 - 1];
                    that.blocks[i0][this.blocks[i0].length - i1 - 1] = this.blocks[i0][i1];
                }
            }
            return that;
        }

        if (axis == axes.getThird()) {
            for (int i0 = 0; i0 < this.blocks.length; i0++) {
                for (int i1 = 0; i1 < this.blocks[i0].length; i1++) {
                    for (int i2 = 0; i2 < this.blocks[i0][i1].length / 2; i2++) {
                        that.blocks[i0][i1][i2] = this.blocks[i0][i1][this.blocks[i0][i1].length - i2 - 1];
                        that.blocks[i0][i1][this.blocks[i0][i1].length - i2 - 1] = this.blocks[i0][i1][i2] ;
                    }
                }
            }
            return that;
        }

        throw new IllegalArgumentException("Invalid axis " + axis);
    }

    @Override
    public Schematic translate(Position offset) {
        AbstractSchematic that = new AbstractSchematic(this, this.size);

        that.offset = offset;

        return that;
    }

    @Override
    public Schematic translate(AxisOrder axisOrder) {
        AbstractSchematic that = new AbstractSchematic(this, this.size);

        that.axes = axisOrder;

        return that;
    }

    @Override
    public Schematic setBlock(int x, int y, int z, BlockData data) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("coordinate out of range");

        Dimensions newSize = new Dimensions(size).floor();

        if (x >= size.width())
            newSize = new Dimensions(x + 1, size.height(), size.depth());

        if (y >= size.height())
            newSize = new Dimensions(size.width(), y + 1, size.depth());

        if (z >= size.depth())
            newSize = new Dimensions(size.width(), size.height(), z + 1);

        AbstractSchematic that = new AbstractSchematic(this, newSize);

        Position pos = new Position(x, y, z);

        that.blocks[(int) axes.getFirst().getAlong(pos)][(int) axes.getSecond().getAlong(pos)][(int) axes.getThird().getAlong(pos)] = data;

        return that;
    }

    @Override
    public Schematic removeBlock(int x, int y, int z) {
        return null; // TODO
    }
}
