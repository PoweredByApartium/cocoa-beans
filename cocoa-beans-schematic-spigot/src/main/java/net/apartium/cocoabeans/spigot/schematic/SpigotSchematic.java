package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematic;
import net.apartium.cocoabeans.schematic.BlockData;
import net.apartium.cocoabeans.schematic.Dimensions;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Iterator;

import static net.apartium.cocoabeans.spigot.schematic.SpigotSchematicHelper.toBukkit;


public class SpigotSchematic extends AbstractSchematic {

    public SpigotSchematic() {
        super();
    }

    public SpigotSchematic(AbstractSchematic that, Dimensions size, AxisOrder axes) {
        super(that, size, axes);
    }

    public void paste(final Location origin) {
        Iterator<Entry<Position, BlockData>> iterator = this.blocksIterator();
        while (iterator.hasNext()) {
            Entry<Position, BlockData> entry = iterator.next();
            Position position = entry.key();
            Block block = new Location(
                    origin.getWorld(),
                    origin.getX() + position.getX() + offset.getX(),
                    origin.getY() + position.getY() + offset.getY(),
                    origin.getZ() + position.getZ() + offset.getZ()
            ).getBlock();

            org.bukkit.block.data.BlockData blockData = toBukkit(entry.value());
            if (blockData == null) {
                Bukkit.getLogger().warning("Could not convert block data to org.bukkit.block.data.BlockData! (" + entry.value().type().toString() + ")");
                continue;
            }

            block.setBlockData(blockData);
        }
    }

}
