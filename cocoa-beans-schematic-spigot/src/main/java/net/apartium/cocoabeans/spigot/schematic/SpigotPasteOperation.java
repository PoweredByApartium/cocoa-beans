package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractPasteOperation;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.function.Function;

import static net.apartium.cocoabeans.spigot.Locations.toVector;
import static net.apartium.cocoabeans.spigot.schematic.SpigotSchematicHelper.*;

public class SpigotPasteOperation extends AbstractPasteOperation {

    private final Location origin;
    private final Function<Block, Boolean> shouldPlace;

    public SpigotPasteOperation(Location origin, BlockIterator iterator, AxisOrder axisOrder, Function<Block, Boolean> shouldPlace) {
        super(iterator, axisOrder);
        this.origin = origin;
        this.shouldPlace = shouldPlace;
    }

    @Override
    protected void place(BlockPlacement placement) {
        Block block = origin.clone().add(toVector(placement.position())).getBlock();
        if (!shouldPlace.apply(block))
            return;

        // TODO temp my ass
        if (IS_LEGACY) {
            setBlockLegacy(block, placement.block(), false);
            return;
        }

        BlockData blockData = toBukkit(placement.block());
        if (blockData == null) {
            // TODO temp
            Bukkit.getLogger().warning("Could not convert block data to org.bukkit.block.data.BlockData! (" + placement.block().type().toString() + ")");
            return;
        }

        block.setBlockData(blockData);
    }
}
