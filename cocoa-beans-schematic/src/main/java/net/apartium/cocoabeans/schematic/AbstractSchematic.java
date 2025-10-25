package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.schematic.iterator.SortedAxisBlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.Entry;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.*;

@ApiStatus.AvailableSince("0.0.45")
public abstract class AbstractSchematic implements Schematic {

    protected UUID id = UUID.randomUUID();
    protected MinecraftPlatform platform;
    protected Instant created = Instant.now();
    protected String author;
    protected String title;

    protected Position offset = Position.ZERO;
    protected Dimensions size = Dimensions.box(0);
    protected AxisOrder axes = AxisOrder.XYZ;

    protected BlockChunk blockChunk;

    public AbstractSchematic() {
        this.blockChunk = new BlockChunk(axes, 0, Position.ZERO, Position.ZERO);
    }

    public AbstractSchematic(Schematic schematic) {
        this.id = schematic.id();
        this.created = schematic.created();
        this.platform = schematic.platform();
        this.author = schematic.author();
        this.title = schematic.title();

        this.offset = schematic.offset();
        this.size = schematic.size();
        this.axes = this.axisOrder();

        this.blockChunk = new BlockChunk(axes, 1, Position.ZERO, Position.ZERO);
        BlockIterator iterator = schematic.blocksIterator();
        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();
            rescaleChunkIfNeeded(placement.position());
            this.blockChunk.setBlock(placement);
        }
    }

    protected AbstractSchematic(AbstractSchematic that, Dimensions size, AxisOrder axes) {
        this.id = that.id;
        this.created = that.created;
        this.platform = that.platform();
        this.author = that.author;
        this.title = that.title;

        this.offset = new Position(that.offset).floor();
        this.size = new Dimensions(size).floor();
        this.axes = axes;

        this.blockChunk = new BlockChunk(this.axes, 1, Position.ZERO, Position.ZERO);
        BlockChunkIterator iterator = new BlockChunkIterator(that.blockChunk);
        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();

            rescaleChunkIfNeeded(placement.position());
            this.blockChunk.setBlock(placement);
        }
    }

    protected void rescaleChunkIfNeeded(Position pos) {
        int maxAxis = (int) Math.max(pos.getX(), Math.max(pos.getY(), pos.getZ()));
        if (maxAxis >= this.blockChunk.getScaler())
            this.blockChunk = new BlockChunk(axes, Mathf.nextPowerOfFour(maxAxis) * 4, Position.ZERO, Position.ZERO, this.blockChunk);
    }

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
    public BlockIterator blocksIterator() {
        return new BlockChunkIterator(blockChunk);
    }

    @Override
    public BlockIterator sortedIterator(AxisOrder axisOrder) {
        return new SortedAxisBlockIterator(this.blockChunk, size, axisOrder);
    }

}
