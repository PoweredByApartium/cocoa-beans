package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractPasteOperation;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotPasteOperation extends AbstractPasteOperation {

    private final Location origin;
    private final BiFunction<Block, BlockPlacement, Boolean> shouldPlace;
    private final Function<BlockPlacement, BlockData> mapper;
    private final SpigotSchematicPlacer placer;

    public SpigotPasteOperation(Location origin, BlockIterator iterator, AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace, Function<BlockPlacement, BlockData> mapper, SpigotSchematicPlacer placer) {
        super(iterator, axisOrder);
        this.origin = origin;
        this.shouldPlace = shouldPlace;
        this.mapper = mapper;
        this.placer = placer;
    }

    @Override
    protected boolean place(BlockPlacement placement) {
        placement = mapping(placement);
        Block block = origin.clone().add(toVector(placement.position())).getBlock();

        if (!shouldPlace.apply(block, placement))
            return false;

        placer.place(block, placement);
        return true;
    }

    private BlockPlacement mapping(BlockPlacement placement) {
        BlockData result = mapper.apply(placement);
        if (result == placement.block())
            return placement;

        return new BlockPlacement(
                placement.position(),
                result
        );
    }

}
