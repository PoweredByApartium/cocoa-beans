package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CommandBlock;

public class CommandBlockConditionalProp extends BooleanBlockProp implements SpigotPropHandler {

    public CommandBlockConditionalProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof CommandBlock commandBlock))
            return;

        commandBlock.setConditional(value);
    }

}
