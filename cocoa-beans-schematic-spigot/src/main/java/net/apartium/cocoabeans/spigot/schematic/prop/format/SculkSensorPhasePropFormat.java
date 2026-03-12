package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.SculkSensorPhaseProp;
import org.bukkit.block.data.type.SculkSensor;

import java.util.function.Function;

public class SculkSensorPhasePropFormat extends EnumPropFormat<SculkSensor.Phase> {

    public static final SculkSensorPhasePropFormat INSTANCE = new SculkSensorPhasePropFormat();

    private SculkSensorPhasePropFormat() {
        this(SculkSensorPhaseProp::new);
    }

    public SculkSensorPhasePropFormat(Function<SculkSensor.Phase, BlockProp<SculkSensor.Phase>> constructor) {
        super(SculkSensor.Phase.class, SculkSensor.Phase::values, constructor);
    }

}
