package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.time.Instant;
import java.util.*;

@ApiStatus.AvailableSince("0.0.45")
public class AbstractSchematic implements Schematic {

    protected UUID id = UUID.randomUUID();
    protected Instant created = Instant.now();
    protected String author;
    protected String title;

    protected Position offset = Position.ZERO;
    protected Dimensions size = Dimensions.box(0);
    protected AxisOrder axes = AxisOrder.XYZ;

    protected BlockChunk blockChunk;

    public AbstractSchematic() {
        this.blockChunk = new BlockChunk(axes, Math.pow(4, 6), Position.ZERO, Position.ZERO);
    }

    protected AbstractSchematic(AbstractSchematic that, Dimensions size, AxisOrder axes) {
        this.id = that.id;
        this.created = that.created;
        this.author = that.author;
        this.title = that.title;

        this.offset = new Position(that.offset).floor();
        this.size = new Dimensions(size).floor();
        this.axes = axes;

        this.blockChunk = new BlockChunk(this.axes, Math.pow(4, 6), Position.ZERO, Position.ZERO);
        for (Entry<Position, BlockData> entry : that.blockChunk) {
            this.blockChunk.setBlock(entry.key(), entry.value());
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

        return blockChunk.getBlock(pos);
    }

    @Override
    public Iterator<Entry<Position, BlockData>> blocksIterator() {
        return blockChunk.iterator();
    }

    @Override
    public SchematicBuilder builder() {
        return new AbstractSchematicBuilder(this);
    }

}
