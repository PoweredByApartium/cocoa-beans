package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.schematic.prop.ByteBlockProp;
import net.apartium.cocoabeans.schematic.prop.RotatableProp;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.AvailableSince("0.0.46")
public class LegacyDataProp extends ByteBlockProp implements RotatableProp<LegacyDataProp> {

    public LegacyDataProp(byte value) {
        super(value);
    }

    @Override
    public LegacyDataProp rotate(@NotNull NamespacedKey type, int degrees) {
        if (!type.namespace().equals("minecraft"))
            return this;

        return switch (type.key().toLowerCase()) {
            case "ladder" -> rotateForLadder(degrees);
            default -> this;
        };
    }

    private LegacyDataProp rotateForLadder(int degrees) {
        if (degrees == 0)
            return this;


        int currentDegrees = switch(value) {
            case 3 -> 180;
            case 4 -> 270;
            case 5 -> 90;
            default -> 0;
        };

        degrees = (currentDegrees + degrees) % 360;
        return new LegacyDataProp((byte) switch (degrees) {
            case 90 -> 5;
            case 180 -> 3;
            case 270 -> 4;
            default -> 2;
        });
    }
}
