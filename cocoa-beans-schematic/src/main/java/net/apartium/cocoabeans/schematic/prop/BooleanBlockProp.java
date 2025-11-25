package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a boolean block property
 */
@ApiStatus.AvailableSince("0.0.46")
public class BooleanBlockProp implements BlockProp<Boolean> {

    protected final boolean value;


    /**
     * Construct a new boolean block prop
     * @param value value
     */
    public BooleanBlockProp(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean value() {
        return value;
    }
}
