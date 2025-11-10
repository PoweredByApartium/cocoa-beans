package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class BooleanBlockProp implements BlockProp<Boolean> {

    private final boolean value;
    public BooleanBlockProp(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean value() {
        return value;
    }
}
