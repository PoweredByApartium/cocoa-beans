package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bell;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public record BellAttachmentProp(Bell.Attachment value) implements BlockProp<Bell.Attachment>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Bell bell))
            return;

        bell.setAttachment(value);
    }

}
