package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicHelper {

    public static final MinecraftVersion VERSION = ServerUtils.getVersion();

    public static SpigotSchematic load(String title, String author, Position playerPos, World world, Position pos0, Position pos1, SpigotSchematicPlacer placer) {
        AreaSize size = new AreaSize(
                Math.abs(pos0.getX() - pos1.getX()) + 1,
                Math.abs(pos0.getY() - pos1.getY()) + 1,
                Math.abs(pos0.getZ() - pos1.getZ()) + 1
        );

        SpigotSchematicBuilder builder = new SpigotSchematicBuilder();

        builder.size(size);

        Position min = Position.min(pos0, pos1);
        Position max = Position.max(pos0, pos1);

        Position offset = new Position(min).subtract(playerPos).floor();

        builder.translate(offset);

        for (int x = (int) min.getX(); x <= max.getX(); x++) {
            for (int y = (int) min.getY(); y <= max.getY(); y++) {
                for (int z = (int) min.getZ(); z <= max.getZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.AIR)
                        continue;

                    try {
                        BlockData blockData = placer.getBlockData(block);
                        builder.setBlock((int) (x - min.getX()), (int) (y - min.getY()), (int) (z - min.getZ()), blockData);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }

        builder.metadata(new SpigotSchematicMetadataBuilder()
                .title(title)
                .author(author)
                .build());
        builder.created(Instant.now());
        builder.platform(new MinecraftPlatform(VERSION, "spigot/paper", "---"));

        return builder.build();
    }

}
