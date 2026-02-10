package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractPasteOperation;
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
import java.util.function.BiPredicate;
import java.util.function.Function;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

/**
 * SpigotPasteOperation is responsible for pasting schematic blocks into a Spigot world.
 * <p>
 * This class extends {@link AbstractPasteOperation} and provides Spigot-specific block placement logic.
 * It allows customization of placement conditions, block mapping, and post-placement actions.
 * <ul>
 *   <li>Origin: The world location where the paste operation starts.</li>
 *   <li>ShouldPlace: Predicate to determine if a block should be placed.</li>
 *   <li>Mapper: Function to map block placements to block data.</li>
 *   <li>Placer: Handles the actual block placement in the world.</li>
 *   <li>PostPlaceActions: Actions to perform after placing each block.</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 *   SpigotPasteOperation op = new SpigotPasteOperation(origin, iterator, axisOrder, shouldPlace, mapper, placer);
 *   op.addPostPlaceAction((block, data) -> { // custom action after placing a block });
 * </pre>
 *
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public class SpigotPasteOperation extends AbstractPasteOperation {
    /**
     * The origin location for the paste operation.
     */
    private final Location origin;
    /**
     * Predicate to determine if a block should be placed.
     */
    private BiPredicate<Block, BlockPlacement> shouldPlace;
    /**
     * Function to map block placements to block data.
     */
    private Function<BlockPlacement, BlockData> mapper;
    /**
     * Handles the actual block placement in the world.
     */
    private final SpigotSchematicPlacer placer;
    /**
     * Actions to perform after placing each block.
     */
    private final List<BiConsumer<Block, BlockData>> postPlaceActions = new ArrayList<>();

    /**
     * Constructs a new SpigotPasteOperation.
     *
     * @param origin      The origin location for the paste operation.
     * @param iterator    Iterator for block placements.
     * @param axisOrder   Axis order for iteration.
     * @param shouldPlace Predicate to determine if a block should be placed.
     * @param mapper      Function to map block placements to block data.
     * @param placer      Handles the actual block placement in the world.
     */
    public SpigotPasteOperation(Location origin, BlockIterator iterator, AxisOrder axisOrder, BiPredicate<Block, BlockPlacement> shouldPlace, Function<BlockPlacement, BlockData> mapper, SpigotSchematicPlacer placer) {
        super(iterator, axisOrder);
        this.origin = origin;
        this.shouldPlace = shouldPlace;
        this.mapper = mapper;
        this.placer = placer;
    }

    /**
     * Sets the predicate to determine if a block should be placed.
     * @param shouldPlace The predicate.
     * @return This operation instance.
     */
    public SpigotPasteOperation setShouldPlace(BiPredicate<Block, BlockPlacement> shouldPlace) {
        this.shouldPlace = shouldPlace;
        return this;
    }

    /**
     * Sets the function to map block placements to block data.
     * @param mapper The mapping function.
     * @return This operation instance.
     */
    public SpigotPasteOperation setMapper(Function<BlockPlacement, BlockData> mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * Adds an action to perform after placing each block.
     * @param action The action to add.
     * @return This operation instance.
     */
    public SpigotPasteOperation addPostPlaceAction(BiConsumer<Block, BlockData> action) {
        postPlaceActions.add(action);
        return this;
    }

    /**
     * Returns the origin location for the paste operation.
     * @return The origin location.
     */
    public Location origin() {
        return origin.clone();
    }

    /**
     * Places a block according to the operation's logic.
     * @param placement The block placement.
     * @return True if the block was placed, false otherwise.
     */
    @Override
    protected boolean place(BlockPlacement placement) {
        placement = mapping(placement);
        Block block = origin.clone().add(toVector(placement.position())).getBlock();

        if (!shouldPlace.test(block, placement))
            return false;

        placer.place(block, placement);

        for (BiConsumer<Block, BlockData> action : postPlaceActions)
            action.accept(block, placement.block());

        return true;
    }

    /**
     * Maps a block placement to block data using the mapper function.
     * @param placement The block placement.
     * @return The mapped block placement.
     */
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
