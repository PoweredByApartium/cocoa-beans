package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

public class LevelledLevelProp extends IntBlockProp implements SpigotPropHandler {

    public LevelledLevelProp(int level) {
        super(level);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Levelled levelled))
            return;

        levelled.setLevel(value);
    }

}
