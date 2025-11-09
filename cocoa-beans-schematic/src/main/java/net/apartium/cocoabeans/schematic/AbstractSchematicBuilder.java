package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.RotatableProp;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractSchematicBuilder<T extends Schematic> implements SchematicBuilder<T> {

    protected MinecraftPlatform platform = new MinecraftPlatform(MinecraftVersion.UNKNOWN, "---", "0.0.0");
    protected Instant created = Instant.now();
    protected SchematicMetadata metadata;
    protected AreaSize size;
    protected BlockChunk blockChunk = new BlockChunk(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
    protected Position offset = Position.ZERO;
    protected AxisOrder axes = AxisOrder.XYZ;

    public AbstractSchematicBuilder() {}

    public AbstractSchematicBuilder(Schematic schematic) {
        this.platform = schematic.originPlatform();
        this.created = schematic.created();
        this.metadata = schematic.metadata();
        this.size = schematic.size();
        this.offset = schematic.offset();
        this.axes = schematic.axisOrder();

        this.blockChunk = new BlockChunk(this.axes, 1, Position.ZERO, Position.ZERO);
        schematic.blocksIterator().forEachRemaining(this::setBlock);
    }

    @Override
    public SchematicBuilder<T> platform(MinecraftPlatform platform) {
        this.platform = platform;
        return this;
    }

    @Override
    public SchematicBuilder<T> created(Instant created) {
        this.created = created;
        return this;
    }

    @Override
    public SchematicBuilder<T> metadata(SchematicMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public SchematicBuilder<T> size(AreaSize size) {
        this.size = size;
        // TODO add checks with block chunk
        return this;
    }


    @Override
    public SchematicBuilder<T> rotate(int degrees) {
        BlockChunk newChunk = new BlockChunk(this.axes, this.blockChunk.getScaler(), this.blockChunk.getActualPos(), this.blockChunk.getChunkPos());

        Iterator<BlockPlacement> iterator = new BlockChunkIterator(this.blockChunk);

        degrees = Math.abs(degrees + 360) % 360;

        final boolean use0Degrees = degrees == 0;
        final boolean use90Degrees = degrees == 90;
        final boolean use180Degrees = degrees == 180;
        final boolean use270Degrees = degrees == 270;

        final int sizeX = (int) this.size.width();
        final int sizeZ = (int) this.size.depth();

        if (!use0Degrees && !use90Degrees && !use180Degrees && !use270Degrees)
            throw new IllegalArgumentException("no!");


        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();

            Position pos = placement.position();
            int lx = (int) pos.getX();
            int lz = (int) pos.getZ();

            int nx, nz;
            if (use0Degrees) {
                nx = lx;
                nz = lz;
            } else if (use90Degrees) {
                nx = lz;
                nz = (sizeX - 1) - lx;
            } else if (use180Degrees) {
                nx = (sizeX - 1) - lx;
                nz = (sizeZ - 1) - lz;
            } else {
                nx = (sizeZ - 1) - lz;
                nz = lx;
            }

            BlockData data = placement.block();
            Map<String, BlockProp<?>> props = new HashMap<>();
            for (Map.Entry<String, BlockProp<?>> prop : data.props().entrySet()) {
                if (!(prop.getValue() instanceof RotatableProp<?> rotatableProp)) {
                    props.put(prop.getKey(), prop.getValue());
                    continue;
                }

                props.put(prop.getKey(), (BlockProp<?>) rotatableProp.rotate(data.type(), degrees));
            }

            newChunk.setBlock(new BlockPlacement(
                    new Position(nx, pos.getY(), nz),
                    new GenericBlockData(data.type(), props))
            );
        }

        if (use90Degrees) {
            this.offset = new Position(
                    this.offset.getZ(),
                    this.offset.getY(),
                    -this.size.width() - this.offset.getX() + 1
            );
            this.size = new AreaSize(this.size.depth(), this.size.height(), this.size.width());
        } else if  (use180Degrees) {
            this.offset = new Position(
                    -this.size.width() - this.offset.getX() + 1,
                    this.offset.getY(),
                    -this.size.depth() - this.offset.getZ() + 1
            );

            this.size = new AreaSize(this.size.width(), this.size.height(), this.size.depth());
        } else if (use270Degrees) {
            this.size = new AreaSize(this.size.depth(), this.size.height(), this.size.width());
            this.offset = new Position(
                    -this.size.depth() - this.offset.getZ() + 2,
                    this.offset.getY(),
                    this.offset.getX()
            );
        }

        this.blockChunk = newChunk;
        return this;
    }

    @Override
    public SchematicBuilder<T> flip(@NonNls @NotNull Axis axis) {
        BlockChunk newChunk = new BlockChunk(this.axes, this.blockChunk.getScaler(), this.blockChunk.getActualPos(), this.blockChunk.getChunkPos());

        Iterator<BlockPlacement> iterator = new BlockChunkIterator(this.blockChunk);
        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();

            Position pos = placement.position();
            pos = switch (axis) {
                case X -> new Position(this.size.width() - 1 - pos.getX(), pos.getY(), pos.getZ());
                case Y -> new Position(pos.getX(), this.size.height() - 1 - pos.getY(), pos.getZ());
                case Z -> new Position(pos.getX(), pos.getY(), this.size.depth() - 1 - pos.getZ());
            };

            newChunk.setBlock(new BlockPlacement(
                    pos,
                    placement.block()
            ));
        }

        // TODO add offset

        this.blockChunk = newChunk;
        return this;
    }

    @Override
    public SchematicBuilder<T> translate(Position offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public SchematicBuilder<T> translate(AxisOrder axisOrder) {
        BlockChunkIterator iterator = new BlockChunkIterator(this.blockChunk);
        this.axes = axisOrder;
        this.blockChunk = new BlockChunk(this.axes, 1, Position.ZERO, Position.ZERO);
        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();

            rescaleChunkIfNeeded(placement.position());
            this.blockChunk.setBlock(placement);
        }
        return this;
    }

    @Override
    public SchematicBuilder<T> shift(Axis axis, int amount) {
        BlockChunkIterator iterator = new BlockChunkIterator(this.blockChunk);
        this.blockChunk = new BlockChunk(this.axes, 1, Position.ZERO, Position.ZERO);
        while (iterator.hasNext()) {
            BlockPlacement placement = iterator.next();
            Position position = new Position(placement.position());

            switch (axis) {
                case X -> position.add(new Position(amount, 0, 0));
                case Y -> position.add(new Position(0, amount, 0));
                case Z -> position.add(new Position(0, 0, amount));
            }

            placement = new BlockPlacement(
                    position,
                    placement.block()
            );

            rescaleSizeIfNeeded((int) position.getX(), (int) position.getY(), (int) position.getZ());
            rescaleChunkIfNeeded(placement.position());
            this.blockChunk.setBlock(placement);
        }

        return this;
    }

    @Override
    public SchematicBuilder<T> setBlock(BlockPlacement placement) {
        Position pos = placement.position();
        this.setBlock(
                (int) pos.getX(),
                (int) pos.getY(),
                (int) pos.getZ(),
                placement.block()
        );

        return this;
    }

    @Override
    public SchematicBuilder<T> setBlock(int x, int y, int z, BlockData data) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("coordinate out of range");


        Position pos = new Position(x, y, z);
        this.rescaleSizeIfNeeded(x, y, z);
        this.rescaleChunkIfNeeded(pos);
        this.blockChunk.setBlock(new BlockPlacement(
                pos,
                data
        ));

        return this;
    }

    protected void rescaleSize() {
        this.size = blockChunk.getSizeOfEntireChunk();
    }

    protected void rescaleSizeIfNeeded(int x, int y, int z) {
        AreaSize newSize = new AreaSize(size).floor();

        if (x >= size.width())
            newSize = new AreaSize(x + 1, size.height(), size.depth());

        if (y >= size.height())
            newSize = new AreaSize(size.width(), y + 1, size.depth());

        if (z >= size.depth())
            newSize = new AreaSize(size.width(), size.height(), z + 1);

        this.size = newSize;
    }

    protected void rescaleChunkIfNeeded(Position pos) {
        int maxAxis = (int) Math.max(pos.getX(), Math.max(pos.getY(), pos.getZ()));
        if (maxAxis >= this.blockChunk.getScaler())
            this.blockChunk = new BlockChunk(axes, Mathf.nextPowerOfFour(maxAxis) * 4, Position.ZERO, Position.ZERO, this.blockChunk);
    }

    @Override
    public SchematicBuilder<T> removeBlock(int x, int y, int z) {
        Position pos = new Position(x, y, z);
        blockChunk.removeBlock(pos);

        while (this.blockChunk.getMask() == 1) {
            if (!(this.blockChunk.getPointers()[0] instanceof ChunkPointer chunkPointer))
                break;

            this.blockChunk = chunkPointer.getChunk();
        }

        this.rescaleSize();
        return this;
    }

}
