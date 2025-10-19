package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.BellAttachmentProp;
import org.bukkit.block.data.type.Bell;

public class BellAttachmentPropFormat extends EnumPropFormat<Bell.Attachment> {

    public static final BellAttachmentPropFormat INSTANCE = new BellAttachmentPropFormat();

    private BellAttachmentPropFormat() {
        super(
                Bell.Attachment.class,
                Bell.Attachment::values,
                BellAttachmentProp::new
        );
    }
}
