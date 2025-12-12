package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractPasteOperation;
import net.apartium.cocoabeans.schematic.PasteOperation;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotPasteOperation extends AbstractPasteOperation {

    private final Location origin;
    private BiFunction<Block, BlockPlacement, Boolean> shouldPlace;
    private Function<BlockPlacement, BlockData> mapper;
    private final SpigotSchematicPlacer placer;
    private final List<BiConsumer<Block, BlockData>> postPlaceActions = new ArrayList<>();

    public SpigotPasteOperation(Location origin, BlockIterator iterator, AxisOrder axisOrder, BiFunction<Block, BlockPlacement, Boolean> shouldPlace, Function<BlockPlacement, BlockData> mapper, SpigotSchematicPlacer placer) {
        super(iterator, axisOrder);
        this.origin = origin;
        this.shouldPlace = shouldPlace;
        this.mapper = mapper;
        this.placer = placer;
    }

    public SpigotPasteOperation setShouldPlace(BiFunction<Block, BlockPlacement, Boolean> shouldPlace) {
        this.shouldPlace = shouldPlace;
        return this;
    }

    public SpigotPasteOperation setMapper(Function<BlockPlacement, BlockData> mapper) {
        this.mapper = mapper;
        return this;
    }

    public SpigotPasteOperation addPostPlaceAction(BiConsumer<Block, BlockData> action) {
        postPlaceActions.add(action);
        return this;
    }

    @Override
    protected boolean place(BlockPlacement placement) {
        placement = mapping(placement);
        Block block = origin.clone().add(toVector(placement.position())).getBlock();

        if (!shouldPlace.apply(block, placement))
            return false;

        placer.place(block, placement);

        for (BiConsumer<Block, BlockData> action : postPlaceActions)
            action.accept(block, placement.block());

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
