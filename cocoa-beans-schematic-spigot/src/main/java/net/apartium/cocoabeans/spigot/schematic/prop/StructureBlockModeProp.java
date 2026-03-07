package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.StructureBlock;

public record StructureBlockModeProp(StructureBlock.Mode mode) implements BlockProp<StructureBlock.Mode>, SpigotPropHandler {

    @Override
    public StructureBlock.Mode value() {
        return mode;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof StructureBlock structureBlock))
            return;

        structureBlock.setMode(mode);
    }

}
