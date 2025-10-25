package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.Nullable;


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
