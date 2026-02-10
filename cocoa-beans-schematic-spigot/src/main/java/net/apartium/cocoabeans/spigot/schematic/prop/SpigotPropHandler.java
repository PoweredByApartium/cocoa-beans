package net.apartium.cocoabeans.spigot.schematic.prop;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface SpigotPropHandler {

    void update(BlockData blockData);

}
