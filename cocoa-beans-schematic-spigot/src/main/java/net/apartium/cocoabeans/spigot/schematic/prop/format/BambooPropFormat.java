package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.BambooProp;
import org.bukkit.block.data.type.Bamboo;

public class BambooPropFormat extends EnumPropFormat<Bamboo.Leaves> {

    public static final BambooPropFormat INSTANCE = new BambooPropFormat();

    private BambooPropFormat() {
        super(
                Bamboo.Leaves.class,
                Bamboo.Leaves::values,
                BambooProp::new
        );
    }
}
