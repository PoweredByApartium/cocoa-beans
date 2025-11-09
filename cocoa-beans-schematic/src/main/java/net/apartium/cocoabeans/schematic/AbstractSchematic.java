package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.schematic.iterator.SortedAxisBlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.*;

@ApiStatus.AvailableSince("0.0.46")
public abstract class AbstractSchematic implements Schematic {

    protected MinecraftPlatform platform;
    protected Instant created = Instant.now();
    protected SchematicMetadata metadata;

    protected Position offset = Position.ZERO;
    protected AreaSize size = AreaSize.box(0);
    protected AxisOrder axes = AxisOrder.XYZ;

    protected BlockChunk blockChunk;

    public AbstractSchematic() {
        this.blockChunk = new BlockChunk(axes, 1, Position.ZERO, Position.ZERO);
    }

    public AbstractSchematic(
            MinecraftPlatform platform,
            Instant created,
            SchematicMetadata metadata,
            Position offset,
            AreaSize size,
            AxisOrder axes,
            BlockIterator iterator
    ) {
        this.platform = platform;
        this.created = created;
        this.metadata = metadata;
        this.offset = offset;
        this.size = size;
        this.axes = axes;

        this.blockChunk = new BlockChunk(axes, 1, Position.ZERO, Position.ZERO);
        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();

            rescaleChunkIfNeeded(placement.position());
            this.blockChunk.setBlock(placement);
        }
    }

    public AbstractSchematic(Schematic schematic) {
        this(
                schematic.originPlatform(),
                schematic.created(),
                schematic.metadata(),
                schematic.offset(),
                schematic.size(),
                schematic.axisOrder(),
                schematic.blocksIterator()
        );
    }

    protected void rescaleChunkIfNeeded(Position pos) {
        int maxAxis = (int) Math.max(pos.getX(), Math.max(pos.getY(), pos.getZ()));
        if (maxAxis >= this.blockChunk.getScaler())
            this.blockChunk = new BlockChunk(axes, Mathf.nextPowerOfFour(maxAxis) * 4, Position.ZERO, Position.ZERO, this.blockChunk);
    }

    @Override
    public @NotNull MinecraftPlatform originPlatform() {
        return platform;
    }

    @Override
    public @NotNull Instant created() {
        return created;
    }

    @Override
    public @NonNull SchematicMetadata metadata() {
        return metadata;
    }

    @Override
    public @NotNull Position offset() {
        return offset;
    }

    @Override
    public @NotNull AreaSize size() {
        return size;
    }

    @Override
    public @NotNull AxisOrder axisOrder() {
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
