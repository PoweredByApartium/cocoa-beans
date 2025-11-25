package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface SpigotPropHandler {

    void update(BlockData blockData);

}
