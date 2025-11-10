package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class IntBlockProp implements BlockProp<Integer> {

    protected final int value;
    public IntBlockProp(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
