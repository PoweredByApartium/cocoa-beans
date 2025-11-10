package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public record PasteResult(
        int blockPlaces,
        int leftOver
) {

}
