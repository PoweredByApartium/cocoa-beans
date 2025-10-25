package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.NamespacedKey;
import net.apartium.cocoabeans.schematic.prop.ByteBlockProp;
import net.apartium.cocoabeans.schematic.prop.RotatableProp;

public class LegacyDataProp extends ByteBlockProp implements RotatableProp<LegacyDataProp> {

    public LegacyDataProp(byte value) {
        super(value);
    }

    @Override
    public LegacyDataProp rotate(NamespacedKey type, int degrees) {
        if (!type.namespace().equals("minecraft"))
            return this;

        return switch (type.key().toLowerCase()) {
            case "ladder" -> rotateForLadder(degrees);
            default -> this;
        };
    }

    private LegacyDataProp rotateForLadder(int degrees) {
        // TODO improve this
        if (degrees == 0)
            return this;



        if (value == 2) { // NORTH
            if (degrees == 90)
                return new LegacyDataProp((byte) 4);
            if (degrees == 180)
                return new LegacyDataProp((byte) 3);
            if (degrees == 270)
                return new LegacyDataProp((byte) 5);
        }

        if (value == 3) { // SOUTH
            if (degrees == 90)
                return new LegacyDataProp((byte) 5);
            if (degrees == 180)
                return new LegacyDataProp((byte) 2);
            if (degrees == 270)
                return new LegacyDataProp((byte) 4);
        }

        if (value == 4) { // WEST
            if (degrees == 90)
                return new LegacyDataProp((byte) 3);
            if (degrees == 180)
                return new LegacyDataProp((byte) 5);
            if (degrees == 270)
                return new LegacyDataProp((byte) 2);
        }

        if (value == 5) { // EAST
            if (degrees == 90)
                return new LegacyDataProp((byte) 2);
            if (degrees == 180)
                return new LegacyDataProp((byte) 4);
            if (degrees == 270)
                return new LegacyDataProp((byte) 3);
        }

        return this;
    }
}
