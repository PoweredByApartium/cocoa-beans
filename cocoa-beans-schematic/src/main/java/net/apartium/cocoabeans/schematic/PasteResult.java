package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents the results of a paste operation
 * @see PasteOperation
 * @param blockPlaces how many blocks were placed
 * @param leftOver how many blocks are still left to place
 */
@ApiStatus.AvailableSince("0.0.46")
public record PasteResult(
        int blockPlaces,
        int leftOver
) { }
