package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.PointedDripstoneThicknessProp;
import org.bukkit.block.data.type.PointedDripstone;

import java.util.function.Function;

public class PointedDripstoneThicknessPropFormat extends EnumPropFormat<PointedDripstone.Thickness> {

    public static final PointedDripstoneThicknessPropFormat INSTANCE = new PointedDripstoneThicknessPropFormat(PointedDripstoneThicknessProp::new);

    public PointedDripstoneThicknessPropFormat(Function<PointedDripstone.Thickness, BlockProp<PointedDripstone.Thickness>> constructor) {
        super(
                PointedDripstone.Thickness.class,
                PointedDripstone.Thickness::values,
                constructor
        );
    }
}
