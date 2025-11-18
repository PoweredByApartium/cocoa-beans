package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EndPortalFrame;

public class EndPortalFrameEyeProp extends BooleanBlockProp implements SpigotPropHandler {

    public EndPortalFrameEyeProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof EndPortalFrame endPortalFrame))
            return;

        endPortalFrame.setEye(value);
    }

}
