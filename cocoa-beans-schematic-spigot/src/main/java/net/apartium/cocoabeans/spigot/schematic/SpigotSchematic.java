package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematic extends AbstractSchematic {

    public SpigotSchematic() {
        super();
    }

    public SpigotSchematic(MinecraftPlatform platform, Instant created, SchematicMetadata metadata, Position offset, AreaSize size, AxisOrder axes, BlockIterator iterator) {
        super(platform, created, metadata, offset, size, axes, iterator);
    }

    public SpigotSchematic(Schematic schematic) {
        super(schematic);
    }

    public PasteOperation paste(final Location origin) {
        return paste(origin, axisOrder());
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder) {
        return paste(origin, axisOrder, (block, blockPlacement) -> block.getType() == Material.AIR);
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace) {
        return paste(origin, axisOrder, shouldPlace, BlockPlacement::block);
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace, Function<BlockPlacement, BlockData> mapper) {
        return paste(origin, axisOrder, shouldPlace, mapper, SpigotSchematicPlacer.getInstance());
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace, Function<BlockPlacement, BlockData> mapper, SpigotSchematicPlacer placer) {
        return new SpigotPasteOperation(
                origin.clone().add(toVector(offset)),
                sortedIterator(axisOrder),
                axisOrder,
                shouldPlace,
                mapper,
                placer
        );
    }

    @Override
    public SpigotSchematicBuilder toBuilder() {
        return new SpigotSchematicBuilder(this);
    }
}
