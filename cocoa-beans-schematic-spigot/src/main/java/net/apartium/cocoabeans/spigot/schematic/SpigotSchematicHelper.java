package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.SchematicMetadata;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.ServerUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Utility class for creating and loading {@link SpigotSchematic} instances from a region in a Bukkit {@link World}.
 * <p>
 * This class provides methods to extract schematic data from a specified area, including block data and metadata.
 * </p>
 * <p>
 * Usage is static-only; instantiation is not allowed.
 * </p>
 */
@ApiStatus.AvailableSince("0.0.46")
@NullMarked
public class SpigotSchematicHelper {
    /**
     * Loads a schematic from a specified region in the given world.
     *
     * @param title      The schematic title.
     * @param author     The schematic author.
     * @param playerPos  The position of the player (used for offset calculation).
     * @param world      The Bukkit world to extract blocks from.
     * @param pos0       One corner of the region.
     * @param pos1       The opposite corner of the region.
     * @param placer     The placer used to convert blocks to schematic block data.
     * @return           The loaded {@link SpigotSchematic}, or {@code null} if an error occurs during extraction.
     */
    public static @Nullable SpigotSchematic load(String title, String author, Position playerPos, World world, Position pos0, Position pos1, SpigotSchematicPlacer placer) {
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

        builder.metadata(SchematicMetadata.builder()
                .title(title)
                .author(author)
                .build());
        builder.created(Instant.now());
        builder.platform(ServerUtils.getPlatform());

        return builder.build();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private SpigotSchematicHelper() {}
}
