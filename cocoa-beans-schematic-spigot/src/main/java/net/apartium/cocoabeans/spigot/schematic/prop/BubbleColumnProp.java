package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BubbleColumn;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class BubbleColumnProp extends BooleanBlockProp implements SpigotPropHandler {

    public BubbleColumnProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof BubbleColumn bubbleColumn))
            return;

        bubbleColumn.setDrag(value);
    }

}
