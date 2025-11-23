package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWire;

import java.util.Map;

public record RedstoneWireConnectionsProp(Map<BlockFace, RedstoneWire.Connection> connections) implements BlockProp<Map<BlockFace, RedstoneWire.Connection>>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof RedstoneWire wire))
            return;

        for (Map.Entry<BlockFace, RedstoneWire.Connection> connection : connections.entrySet())
            wire.setFace(connection.getKey(), connection.getValue());
    }

    @Override
    public Map<BlockFace, RedstoneWire.Connection> value() {
        return connections;
    }
}
