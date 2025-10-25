package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.function.Function;

import static net.apartium.cocoabeans.spigot.Locations.toVector;
import static net.apartium.cocoabeans.spigot.schematic.SpigotSchematicHelper.*;


public class SpigotSchematic extends AbstractSchematic {

    public SpigotSchematic() {
        super();
    }

    public SpigotSchematic(AbstractSchematic schematic) {
        this(schematic, schematic.size(), schematic.axisOrder());
    }

    public SpigotSchematic(AbstractSchematic that, Dimensions size, AxisOrder axes) {
        super(that, size, axes);
    }

    public PasteOperation paste(final Location origin) {
        return paste(origin, axisOrder());
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder) {
        return paste(origin, axisOrder, block -> block.getType() == Material.AIR);
    }

    public PasteOperation paste(final Location origin, final AxisOrder axisOrder, Function<Block, Boolean> shouldPlace) {
        return new SpigotPasteOperation(
                origin.clone().add(toVector(offset)),
                sortedIterator(axisOrder),
                axisOrder,
                shouldPlace
        );
    }

    @Override
    public SpigotSchematicBuilder toBuilder() {
        return new SpigotSchematicBuilder(this);
    }
}
