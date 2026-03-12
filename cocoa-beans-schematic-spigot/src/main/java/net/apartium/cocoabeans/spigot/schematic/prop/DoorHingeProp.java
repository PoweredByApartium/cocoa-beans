package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

public record DoorHingeProp(Door.Hinge value) implements BlockProp<Door.Hinge>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Door door))
            return;

        door.setHinge(value);
    }

}
