package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkSensor;

public record SculkSensorPhaseProp(SculkSensor.Phase phase) implements BlockProp<SculkSensor.Phase>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof SculkSensor sculkSensor))
            return;

        sculkSensor.setPhase(phase);
    }

    @Override
    public SculkSensor.Phase value() {
        return phase;
    }
}
