package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.DaylightDetector;

public class DayLightDetectorInvertedProp extends BooleanBlockProp implements SpigotPropHandler {

    public DayLightDetectorInvertedProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof DaylightDetector daylightDetector))
            return;

        daylightDetector.setInverted(value);
    }

}
