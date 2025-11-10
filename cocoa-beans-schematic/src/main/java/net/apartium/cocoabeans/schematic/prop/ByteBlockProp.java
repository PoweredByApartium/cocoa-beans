package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class ByteBlockProp implements BlockProp<Byte> {

    protected final byte value;

    public ByteBlockProp(byte value) {
        this.value = value;
    }

    @Override
    public Byte value() {
        return value;
    }

}
