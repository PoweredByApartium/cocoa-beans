package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import org.bukkit.block.BlockFace;

import java.util.function.Function;

public class BlockFacePropFormat extends EnumPropFormat<BlockFace> {

    public BlockFacePropFormat(Function<BlockFace, BlockProp<BlockFace>> constructor) {
        super(
                BlockFace.class,
                BlockFace::values,
                constructor
        );
    }
}
