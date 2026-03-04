package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.format.BodyExtension;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.schematic.iterator.SortedAxisBlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Set;

/**
 * @hidden
 */
@ApiStatus.Internal
@NullMarked
public abstract class AbstractSchematic implements Schematic {

    protected final MinecraftPlatform platform;
    protected final Instant created;
    protected final SchematicMetadata metadata;

    protected final Position offset;
    protected final AreaSize size;
    protected final AxisOrder axes;

    protected final BlockChunk blockChunk;

    protected AbstractSchematic(
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

        this.blockChunk = iterator.toBlockChunk(axes);

    }

    protected AbstractSchematic(Schematic schematic) {
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


    @Override
    public MinecraftPlatform originPlatform() {
        return platform;
    }

    @Override
    public Instant created() {
        return created;
    }

    @Override
    public SchematicMetadata metadata() {
        return metadata;
    }

    @Override
    public Position offset() {
        return offset;
    }

    @Override
    public AreaSize size() {
        return size;
    }

    @Override
    public AxisOrder axisOrder() {
        return axes;
    }

    @Override
    public Set<BodyExtension<?>> bodyExtensions() {
        return Set.of();
    }

    @Override
    public @Nullable BlockData getBlockData(int x, int y, int z) {
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

    @Override
    public BlockIterator reverseIterator(AxisOrder axisOrder, Set<Axis> reverseAxis) {
        return new SortedAxisBlockIterator(this.blockChunk, size, axisOrder, reverseAxis);
    }

}
