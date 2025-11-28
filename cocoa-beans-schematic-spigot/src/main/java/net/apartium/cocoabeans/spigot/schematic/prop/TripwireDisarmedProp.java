package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;

public class TripwireDisarmedProp extends BooleanBlockProp implements SpigotPropHandler {

    public TripwireDisarmedProp(boolean disarmed) {
        super(disarmed);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Tripwire tripwire))
            return;

        tripwire.setDisarmed(value);
    }

}
