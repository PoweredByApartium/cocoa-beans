package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;


@ApiStatus.AvailableSince("0.0.46")
public interface PasteOperation {

    boolean hasNext();
    @Nullable Position current();

    PasteResult performAll();
    PasteResult performAllOnDualAxis();
    PasteResult performAllOnSingleAxis();

    PasteResult advanceAllAxis(int numOfBlocks);
    PasteResult advanceOnDualAxis(int numOfBlocks);
    PasteResult advanceOnSingleAxis(int numOfBlocks);

}
