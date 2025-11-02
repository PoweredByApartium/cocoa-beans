package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public interface SchematicBuilder<T extends Schematic> {

    SchematicBuilder<T> id(UUID id);

    SchematicBuilder<T> platform(MinecraftPlatform platform);
    SchematicBuilder<T> created(Instant created);

    SchematicBuilder<T> author(String author);
    SchematicBuilder<T> title(String title);

    SchematicBuilder<T> size(Dimensions size);

    SchematicBuilder<T> rotate(int degrees);
    SchematicBuilder<T> flip(@NotNull Axis axis);

    SchematicBuilder<T> translate(Position offset);
    SchematicBuilder<T> translate(AxisOrder axisOrder);

    SchematicBuilder<T> setBlock(BlockPlacement placement);
    SchematicBuilder<T> setBlock(int x, int y, int z, BlockData data);
    SchematicBuilder<T> removeBlock(int x, int y, int z);

    T build();

}
