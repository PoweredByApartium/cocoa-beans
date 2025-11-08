package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

public class SpigotSchematic extends AbstractSchematic {

    public SpigotSchematic() {
        super();
    }

    public SpigotSchematic(UUID id, MinecraftPlatform platform, Instant created, String author, String title, Position offset, Dimensions size, AxisOrder axes, BlockIterator iterator) {
        super(id, platform, created, author, title, offset, size, axes, iterator);
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
        return paste(origin, axisOrder, shouldPlace, SpigotSchematicPlacer.getInstance());
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace, SpigotSchematicPlacer placer) {
        return new SpigotPasteOperation(
                origin.clone().add(toVector(offset)),
                sortedIterator(axisOrder),
                axisOrder,
                shouldPlace,
                placer
        );
    }

    @Override
    public SpigotSchematicBuilder toBuilder() {
        return new SpigotSchematicBuilder(this);
    }
}
