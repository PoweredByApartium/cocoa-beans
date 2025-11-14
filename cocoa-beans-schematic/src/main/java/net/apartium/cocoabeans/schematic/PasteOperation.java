package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a schematic paste operation
 * @see Schematic
 * @see PasteResult
 */
@ApiStatus.AvailableSince("0.0.46")
public interface PasteOperation {

    /**
     * Check if there are any more blocks to paste
     * @return whether there are blocks remaining in this operation
     */
    boolean hasNext();

    /**
     * The current paste position for this operation
     * TODO kfir this needs claryifying
     * @return current paste position
     */
    @Nullable Position current();

    /**
     * Paste all remaining blocks in their natural order
     * @return paste result
     */
    PasteResult performAll();

    /**
     * Paste all remaining blocks on dual axis
     * TODO kfir this needs claryifying
     * @return paste result
     */
    PasteResult performAllOnDualAxis();

    /**
     * Paste all remaining blocks on single axis
     * TODO kfir this needs claryifying
     * @return paste result
     */
    PasteResult performAllOnSingleAxis();

    /**
     * Paste specified number of blocks on all axis
     * @param numOfBlocks number of blocks to paste
     * TODO kfir this needs claryifying
     * @return paste result
     */
    PasteResult advanceAllAxis(int numOfBlocks);

    /**
     * Paste specified number of blocks on dual axis
     * @param numOfBlocks number of blocks to paste
     * TODO kfir this needs claryifying
     * @return paste result
     */
    PasteResult advanceOnDualAxis(int numOfBlocks);

    /**
     * Paste specified number of blocks on single axis
     * @param numOfBlocks number of blocks to paste
     * TODO kfir this needs claryifying
     * @return paste result
     */
    PasteResult advanceOnSingleAxis(int numOfBlocks);

}
