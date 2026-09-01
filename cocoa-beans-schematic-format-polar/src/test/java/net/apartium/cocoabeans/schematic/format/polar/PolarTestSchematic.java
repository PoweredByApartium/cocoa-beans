package net.apartium.cocoabeans.schematic.format.polar;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.format.BodyExtension;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class PolarTestSchematic extends AbstractSchematic {

    PolarTestSchematic(MinecraftPlatform platform, Instant created, SchematicMetadata metadata,
                       Position offset, AreaSize size, AxisOrder axes, BlockIterator iterator) {
        super(platform, created, metadata, offset, size, axes, iterator, Set.of());
    }

    @Override
    public SchematicBuilder toBuilder() {
        throw new UnsupportedOperationException();
    }

}