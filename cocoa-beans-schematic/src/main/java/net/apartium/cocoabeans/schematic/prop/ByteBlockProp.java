package net.apartium.cocoabeans.schematic.prop;

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
