package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.axis.Axis;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.Entry;

import java.time.Instant;
import java.util.UUID;

public class AbstractSchematicBuilder extends AbstractSchematic implements SchematicBuilder {

    public AbstractSchematicBuilder(AbstractSchematic schematic) {
        super(schematic, schematic.size(), schematic.axisOrder());
    }

    @Override
    public SchematicBuilder id(UUID id) {
        this.id = id;
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
        return this;
    }


    @Override
    public SchematicBuilder rotate(AxisOrder axisOrder) {
        // TODO
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
        BlockChunk old = this.blockChunk;
        this.axes = axisOrder;
        this.blockChunk = new BlockChunk(this.axes, Math.pow(4, 6), Position.ZERO, Position.ZERO);
        for (Entry<Position, BlockData> entry : old)
            this.blockChunk.setBlock(entry.key(), entry.value());

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
        this.blockChunk.setBlock(pos, data);

        return this;
    }

    @Override
    public SchematicBuilder removeBlock(int x, int y, int z) {
        Position pos = new Position(x, y, z);
        this.blockChunk.setBlock(pos, null);
        return this;
    }

    @Override
    public Schematic build() {
        return new AbstractSchematic(this, size, axes);
    }
}
