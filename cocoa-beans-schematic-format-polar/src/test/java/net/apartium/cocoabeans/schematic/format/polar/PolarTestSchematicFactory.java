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
import net.apartium.cocoabeans.structs.MinecraftVersion;

import java.time.Instant;
import java.util.Map;

class PolarTestSchematicFactory implements SchematicFactory<PolarTestSchematic> {

    @Override
    public SchematicBuilder createSchematic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PolarTestSchematic createSchematic(
            Instant created,
            MinecraftPlatform platform,
            SchematicMetadata metadata,
            BlockIterator blocks,
            AreaSize size,
            AxisOrder axisOrder,
            Position offset,
            Map<Long, BodyExtension<?>> bodyExtensions
    ) {
        return new PolarTestSchematic(platform, created, metadata, offset, size, axisOrder, blocks);
    }

}