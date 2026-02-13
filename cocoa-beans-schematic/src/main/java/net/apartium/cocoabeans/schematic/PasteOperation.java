package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a schematic paste operation
 * @see Schematic
 * @see PasteResult
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public interface PasteOperation {

    /**
     * Check if there are any more blocks to paste
     * @return whether there are blocks remaining in this operation
     */
    boolean hasNext();

    /**
     * Returns the next placement position for this operation.
     * This method does not alter the state of the operation
     * @return next paste position
     */
    @Nullable Position getNextPosition();

    /**
     * Paste all remaining blocks in their natural order
     * @return paste result
     */
    PasteResult performAll();

    /**
     * Paste all remaining blocks on single axis.
     * For example, if the axis order is {@link net.apartium.cocoabeans.space.axis.AxisOrder#XYZ}, the paste will be on the Z axis
     * @see net.apartium.cocoabeans.space.axis.AxisOrder
     * @return paste result
     */
    PasteResult performAllOnSingleAxis();

    /**
     * Paste all remaining blocks on dual axis
     * For example, if the axis order is {@link net.apartium.cocoabeans.space.axis.AxisOrder#XYZ}, the paste will be on the Y and Z axis simultaniously
     * @return paste result
     */
    PasteResult performAllOnDualAxis();

    /**
     * Paste specified-number of blocks on all axis (X, Y, Z), advancing by the order of the axis
     * @param numOfBlocks number of blocks to paste
     * @return paste result
     */
    PasteResult advanceAllAxis(int numOfBlocks);

    /**
     * Paste specified-number of blocks on dual axis
     * For example, if the axis order is {@link net.apartium.cocoabeans.space.axis.AxisOrder#XYZ}, the paste will be on the Y and Z axis simultaniously
     * @see net.apartium.cocoabeans.space.axis.AxisOrder
     * @param numOfBlocks number of blocks to paste
     * @return paste result
     */
    PasteResult advanceOnDualAxis(int numOfBlocks);

    /**
     * Paste specified number of blocks on single axis
     * For example, if the axis order is {@link net.apartium.cocoabeans.space.axis.AxisOrder#XYZ}, the paste will be on the Z axis
     * @see net.apartium.cocoabeans.space.axis.AxisOrder
     * @param numOfBlocks number of blocks to paste
     * @return paste result
     */
    PasteResult advanceOnSingleAxis(int numOfBlocks);

}
