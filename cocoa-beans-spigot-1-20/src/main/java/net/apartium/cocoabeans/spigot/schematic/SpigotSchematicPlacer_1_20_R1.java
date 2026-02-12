package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.spigot.schematic.prop.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;

import java.util.HashMap;
import java.util.Map;

public class SpigotSchematicPlacer_1_20_R1 implements SpigotSchematicPlacer {

    @Override
    public void place(Block block, BlockPlacement placement) {
        org.bukkit.block.data.BlockData blockData = getBukkitBlockData(placement.block());

        if (blockData == null) {
            Bukkit.getLogger().warning("Could not convert block data to org.bukkit.block.data.BlockData! (" + placement.block().type().toString() + ")");
            return;
        }

        block.setBlockData(blockData);
    }

    private org.bukkit.block.data.BlockData getBukkitBlockData(BlockData blockData) {
        Material type = Material.matchMaterial(blockData.type().namespace() + ":" + blockData.type().key());
        if (type == null || type == Material.AIR)
            return null;


        Map<String, BlockProp<?>> props = blockData.props();
        org.bukkit.block.data.BlockData block = Bukkit.getServer().createBlockData(type);

        for (BlockProp<?> prop : props.values()) {
            if (!(prop instanceof SpigotPropHandler handler))
                continue;

            handler.update(block);
        }

        return block;

    }

    @Override
    public BlockData getBlockData(Block block) {
        Material type = block.getType();
        Map<String, BlockProp<?>> props = new HashMap<>();

        loadProps(block.getBlockData(), props);

        return new GenericBlockData(
                new NamespacedKey(type.key().namespace(), type.key().value()),
                props
        );
    }

    private void loadProps(org.bukkit.block.data.BlockData blockData, Map<String, BlockProp<?>> props) {
        SpigotModernBlockData.add(blockData, props);
    }

}
