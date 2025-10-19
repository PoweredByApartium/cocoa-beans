package net.apartium.cocoabeans.schematic.prop;

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
