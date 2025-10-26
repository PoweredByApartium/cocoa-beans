package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.RotatableProp;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

// TODO remove abstract schematic from extends you lazy fuck
public abstract class AbstractSchematicBuilder<T extends AbstractSchematic> extends AbstractSchematic implements SchematicBuilder {

    public AbstractSchematicBuilder(AbstractSchematic schematic) {
        super(schematic, schematic.size(), schematic.axisOrder());
    }

    @Override
    public SchematicBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    @Override
    public SchematicBuilder platform(MinecraftPlatform platform) {
        this.platform = platform;
        return this;
    }

    @Override
    public SchematicBuilder created(Instant created) {
        this.created = created;
        return this;
    }

    @Override
    public SchematicBuilder author(String author) {
        this.author = author;
        return this;
    }

    @Override
    public SchematicBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public SchematicBuilder size(Dimensions size) {
        this.size = size;
        // TODO add checks with block chunk
        return this;
    }


    @Override
    public SchematicBuilder rotate(int degrees) {
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
            this.size = new Dimensions(this.size.depth(), this.size.height(), this.size.width());
        } else if  (use180Degrees) {
            this.offset = new Position(
                    -this.size.width() - this.offset.getX() + 1,
                    this.offset.getY(),
                    -this.size.depth() - this.offset.getZ() + 1
            );

            this.size = new Dimensions(this.size.width(), this.size.height(), this.size.depth());
        } else if (use270Degrees) {
            this.size = new Dimensions(this.size.depth(), this.size.height(), this.size.width());
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
    public SchematicBuilder flip(Axis axis) {
        // TODO
        return this;
    }

    @Override
    public SchematicBuilder translate(Position offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public SchematicBuilder translate(AxisOrder axisOrder) {
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
    public SchematicBuilder setBlock(int x, int y, int z, BlockData data) {
        if (x < 0 || y < 0 || z < 0)
            throw new IllegalArgumentException("coordinate out of range");

        Dimensions newSize = new Dimensions(size).floor();

        if (x >= size.width())
            newSize = new Dimensions(x + 1, size.height(), size.depth());

        if (y >= size.height())
            newSize = new Dimensions(size.width(), y + 1, size.depth());

        if (z >= size.depth())
            newSize = new Dimensions(size.width(), size.height(), z + 1);


        Position pos = new Position(x, y, z);
        this.size = newSize;

        this.rescaleChunkIfNeeded(pos);
        this.blockChunk.setBlock(new BlockPlacement(
                pos,
                data
        ));

        return this;
    }

    @Override
    public SchematicBuilder removeBlock(int x, int y, int z) {
        Position pos = new Position(x, y, z);
        // TODO rescale down if possible
        this.blockChunk.setBlock(new BlockPlacement(pos, null));
        return this;
    }

}
