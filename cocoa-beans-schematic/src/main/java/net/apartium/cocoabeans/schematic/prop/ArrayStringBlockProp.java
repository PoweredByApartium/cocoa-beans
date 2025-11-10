package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class ArrayStringBlockProp implements BlockProp<String[]> {

    private final String[] values;

    public ArrayStringBlockProp(String[] values) {
        this.values = values;
    }

    @Override
    public String[] value() {
        return values;
    }
}
