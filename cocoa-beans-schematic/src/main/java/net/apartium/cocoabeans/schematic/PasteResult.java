package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents the results of a paste operation
 * @see PasteOperation
 * @param blockPlaces the number of blocks that were placed
 * @param leftOver how many blocks were requested but not placed (applicable when requesting to paste a specific number of blocks)
 */
@ApiStatus.AvailableSince("0.0.46")
public record PasteResult(
        int blockPlaces,
        int leftOver
) { }
