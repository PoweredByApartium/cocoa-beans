package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.schematic.prop.*;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.*;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SpigotSchematicHelper {

    public static final MinecraftVersion VERSION = ServerUtils.getVersion();
    public static final boolean IS_LEGACY = VERSION.isLowerThanOrEqual(MinecraftVersion.V1_12_2);

    public static SpigotSchematic load(String title, String author, Position playerPos, World world, Position pos0, Position pos1) {
        Dimensions size = new Dimensions(
                Math.abs(pos0.getX() - pos1.getX()) + 1,
                Math.abs(pos0.getY() - pos1.getY()) + 1,
                Math.abs(pos0.getZ() - pos1.getZ()) + 1
        );

        SchematicBuilder builder = new SpigotSchematic().toBuilder();


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
                        BlockData blockData = IS_LEGACY
                                ? fromLegacyBukkit(block)
                                : fromBukkit(block.getBlockData());

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

    public static BlockData fromLegacyBukkit(Block block) {
        Material type = block.getType();
        if (type == Material.AIR)
            return null;

        Map<String, BlockProp<?>> props = new HashMap<>(Map.of(
                BlockProp.Legacy.DATA, BlockProp.BYTE.apply(block.getData())
        ));

        BlockState state = block.getState();
        if (state instanceof Sign sign) {
            String[] lines = sign.getLines();
            props.put(BlockProp.Legacy.SIGN_LINES, BlockProp.ARRAY_STRING.apply(lines));
        }

        return new GenericBlockData(
                new NamespacedKey("minecraft", type.name()),
                props
        );
    }

    public static void setBlockLegacy(Block block, BlockData blockData, boolean putAir) {
        Material type = Material.getMaterial(blockData.type().key());
        if (type == null || type == Material.AIR) {
            if (putAir)
                block.setType(Material.AIR);

            return;
        }

        Map<String, BlockProp<?>> props = blockData.props();
        block.setType(type, false);

        if (!props.containsKey(BlockProp.Legacy.DATA))
            return;

        BlockProp<?> prop = props.get("LEGACY_DATA");
        if (!(prop.value() instanceof Byte data))
            return;

        try {
             // TODO change it to correctly way of using version
            block.getClass().getMethod("setData", byte.class, boolean.class).invoke(block, data, false);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        BlockState state = block.getState();
        if (state instanceof Sign sign) {
            prop = props.get(BlockProp.Legacy.SIGN_LINES);
            if (prop == null)
                return;

            if (!(prop.value() instanceof String[] lines))
                throw new IllegalArgumentException("Invalid legacy sign line");


            for (int i = 0; i < lines.length; i++)
                sign.setLine(i, lines[i]);

            sign.update();
            return;
        }
    }

    public static BlockData fromBukkit(org.bukkit.block.data.BlockData blockData) {
        org.bukkit.NamespacedKey type = blockData.getMaterial().getKey();
        Map<String, BlockProp<?>> props = new HashMap<>();


        if (blockData instanceof Stairs stairs)
            props.put(BlockProp.STAIRS_SHAPE, new StairsProp(stairs.getShape()));

        if (blockData instanceof Directional directional)
            props.put(BlockProp.DIRECTIONAL, new DirectionalFaceProp(directional.getFacing()));

        if (blockData instanceof Bamboo bamboo)
            props.put(BlockProp.BAMBOO_LEAVES, new BambooProp(bamboo.getLeaves()));

        if (blockData instanceof Bed bed)
            props.put(BlockProp.BED_PART, new BedPartProp(bed.getPart()));

        if (blockData instanceof Beehive beehive)
            props.put(BlockProp.BEEHIVE_HONEY_LEVEL, new BeeHiveHoneyLevelProp(beehive.getHoneyLevel()));

        if (blockData instanceof Bell bell)
            props.put(BlockProp.BELL_ATTACHMENT, new BellAttachmentProp(bell.getAttachment()));

        if (blockData instanceof BigDripleaf bigDripleaf)
            props.put(BlockProp.BIG_DRIP_LEAF_TILT, new BigDripleafTiltProp(bigDripleaf.getTilt()));


        return new GenericBlockData(
                new NamespacedKey(type.namespace(), type.getKey()),
                props
        );
    }

    public static org.bukkit.block.data.BlockData toBukkit(BlockData blockData) {
        Material type = Material.getMaterial(blockData.type().key().toUpperCase());
        if (type == null || type == Material.AIR) {
            return null;
        }

        Map<String, BlockProp<?>> props = blockData.props();
        org.bukkit.block.data.BlockData block = Bukkit.getServer().createBlockData(type);

        for (BlockProp<?> prop : props.values()) {
            if (!(prop instanceof SpigotPropHandler handler))
                continue;

            handler.update(block);
        }

        return block;
    }

}
