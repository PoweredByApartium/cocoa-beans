package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.spigot.schematic.prop.LegacyDataProp;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class SpigotSchematicPlacer_1_8_R1 implements SpigotSchematicPlacer {

    @Override
    public void place(Block block, BlockPlacement placement) {
        BlockData data = placement.block();
        Material type = Material.getMaterial(data.type().key());

        if (type == null || type == Material.AIR) {
            block.setType(Material.AIR);
            return;
        }

        Map<String, BlockProp<?>> props = data.props();
        block.setType(type, false);

        if (!props.containsKey(BlockProp.Legacy.DATA))
            return;

        BlockProp<?> prop = props.get("LEGACY_DATA");
        if (!(prop.value() instanceof Byte value))
            return;

        block.setData(value);

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

    @Override
    public BlockData getBlockData(Block block) {
        Material type = block.getType();
        if (type == Material.AIR)
            return null;

        Map<String, BlockProp<?>> props = new HashMap<>(Map.of(
                BlockProp.Legacy.DATA, new LegacyDataProp(block.getData())
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

}
