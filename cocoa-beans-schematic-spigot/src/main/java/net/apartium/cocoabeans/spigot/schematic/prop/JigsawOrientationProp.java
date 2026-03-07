package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Jigsaw;

public record JigsawOrientationProp(Jigsaw.Orientation value) implements BlockProp<Jigsaw.Orientation>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Jigsaw jigsaw))
            return;

        jigsaw.setOrientation(value);
    }

}
