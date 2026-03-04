package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.ComparatorModeProp;
import org.bukkit.block.data.type.Comparator;

public class ComparatorModePropFormat extends EnumPropFormat<Comparator.Mode> {

    public static final ComparatorModePropFormat INSTANCE = new ComparatorModePropFormat();

    private ComparatorModePropFormat() {
        super(
                Comparator.Mode.class,
                Comparator.Mode::values,
                ComparatorModeProp::new
        );
    }

}
