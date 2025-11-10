package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class StringBlockProp implements BlockProp<String> {

    private final String value;

    public StringBlockProp(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
