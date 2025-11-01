package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractPasteOperation;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.function.BiFunction;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

public class SpigotPasteOperation extends AbstractPasteOperation {

    private final Location origin;
    private final BiFunction<Block, BlockPlacement, Boolean> shouldPlace;

    public SpigotPasteOperation(Location origin, BlockIterator iterator, AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace) {
        super(iterator, axisOrder);
        this.origin = origin;
        this.shouldPlace = shouldPlace;
    }

    @Override
    protected boolean place(BlockPlacement placement) {
        Block block = origin.clone().add(toVector(placement.position())).getBlock();
        if (!shouldPlace.apply(block, placement))
            return false;

        SpigotSchematicPlacer.INSTANCE.place(block, placement);
        return true;
    }

}
