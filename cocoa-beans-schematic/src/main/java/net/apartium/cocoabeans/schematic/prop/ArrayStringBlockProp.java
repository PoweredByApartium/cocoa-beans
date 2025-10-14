package net.apartium.cocoabeans.schematic.prop;

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
