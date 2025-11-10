package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("0.0.46")
public abstract class AbstractPasteOperation implements PasteOperation {

    protected final BlockIterator iterator;
    protected final AxisOrder axisOrder;

    protected AbstractPasteOperation(BlockIterator iterator, AxisOrder axisOrder) {
        this.iterator = iterator;
        this.axisOrder = axisOrder;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public @Nullable Position current() {
        return iterator.current();
    }

    protected int currentI0() {
        return iterator.current() != null
                ? (int) axisOrder.getFirst().getAlong(current())
                : 0;
    }

    protected int currentI1() {
        return iterator.current() != null
                ? (int) axisOrder.getSecond().getAlong(current())
                : 0;
    }

    @Override
    public PasteResult performAll() {
        int placed = 0;
        while (iterator.hasNext()) {
            if (!place(iterator.next()))
                continue;

            placed++;
        }

        return new PasteResult(
                placed,
                0
        );
    }

    @Override
    public PasteResult performAllOnDualAxis() {
        int startI0 = currentI0();

        int placed = 0;
        while (iterator.hasNext()) {
            if (startI0 != currentI0())
                break;

            if (!place(iterator.next()))
                continue;

            placed++;
        }

        return new PasteResult(
                placed,
                0
        );
    }

    @Override
    public PasteResult performAllOnSingleAxis() {
        int startI0 = currentI0();
        int startI1 = currentI1();

        int placed = 0;
        while (iterator.hasNext()) {
            if (startI0 != currentI0() || startI1 != currentI1())
                break;

            if (!place(iterator.next()))
                continue;

            placed++;
        }

        return new PasteResult(
                placed,
                0
        );
    }

    @Override
    public PasteResult advanceAllAxis(int numOfBlocks) {
        int placed = 0;
        while (iterator.hasNext() && numOfBlocks > 0) {
            if (!place(iterator.next()))
                continue;

            numOfBlocks--;
            placed++;
        }

        return new PasteResult(
                placed,
                numOfBlocks
        );
    }

    @Override
    public PasteResult advanceOnDualAxis(int numOfBlocks) {
        int startI0 = currentI0();

        int placed = 0;
        while (iterator.hasNext() && numOfBlocks > 0) {
            if (startI0 != currentI0())
                break;

            if (!place(iterator.next()))
                continue;

            numOfBlocks--;
            placed++;
        }

        return new PasteResult(
                placed,
                numOfBlocks
        );
    }


    @Override
    public PasteResult advanceOnSingleAxis(int numOfBlocks) {
        int startI0 = currentI0();
        int startI1 = currentI1();

        int placed = 0;
        while (iterator.hasNext() && numOfBlocks > 0) {
            if (startI0 != currentI0() || startI1 != currentI1())
                break;

            if (!place(iterator.next()))
                continue;

            numOfBlocks--;
            placed++;
        }

        return new PasteResult(
                placed,
                numOfBlocks
        );
    }

    protected abstract boolean place(BlockPlacement placement);

}
