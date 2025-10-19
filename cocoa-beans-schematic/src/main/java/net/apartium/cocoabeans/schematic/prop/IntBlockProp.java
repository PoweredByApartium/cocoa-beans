package net.apartium.cocoabeans.schematic.prop;

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
