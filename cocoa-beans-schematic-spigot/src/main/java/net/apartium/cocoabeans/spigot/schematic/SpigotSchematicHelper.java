package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.space.Position;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.time.Instant;
import java.util.Map;

public class SpigotSchematicHelper {

    public static SpigotSchematic load(String title, String author, World world, Position pos0, Position pos1) {
        Dimensions size = new Dimensions(
                Math.abs(pos0.getX() - pos1.getX()) + 1,
                Math.abs(pos0.getY() - pos1.getY()) + 1,
                Math.abs(pos0.getZ() - pos1.getZ()) + 1
        );

        SchematicBuilder builder = new SpigotSchematic().builder();


        Position min = Position.min(pos0, pos1);
        Position max = Position.max(pos0, pos1);

        for (int x = (int) min.getX(); x <= max.getX(); x++) {
            for (int y = (int) min.getY(); y <= max.getY(); y++) {
                for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.AIR)
                        continue;

                    try {
                        BlockData blockData = fromBukkit(block.getBlockData());
                        builder.setBlock((int) (x - min.getX()), (int) (y - min.getY()), (int) (z - min.getZ()), blockData);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }

        builder.title(title);
        builder.author(author);
        builder.created(Instant.now());

        return new SpigotSchematic((AbstractSchematic) builder.build(), size, AxisOrder.XYZ);
    }

    public static BlockData fromBukkit(org.bukkit.block.data.BlockData blockData) {
        org.bukkit.NamespacedKey type = blockData.getMaterial().getKey();
        return new GenericBlockData(
                new NamespacedKey(type.namespace(), type.getKey()),
                Map.of() // TODO
        );
    }

    public static org.bukkit.block.data.BlockData toBukkit(BlockData blockData) {
        Material type = Material.getMaterial(blockData.type().key().toUpperCase());
        if (type == null || type == Material.AIR) {
            return null;
        }

        Map<String, BlockProp<?>> props = blockData.props();
        String data = ""; // todo
        return Bukkit.getServer().createBlockData(type);
    }

}
