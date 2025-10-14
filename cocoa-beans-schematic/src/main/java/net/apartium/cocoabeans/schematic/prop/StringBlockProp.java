package net.apartium.cocoabeans.schematic.prop;

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
