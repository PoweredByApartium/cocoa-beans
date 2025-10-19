package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.StairsProp;
import org.bukkit.block.data.type.Stairs;

public class StairsPropFormat extends EnumPropFormat<Stairs.Shape> {

    public static final StairsPropFormat INSTANCE = new StairsPropFormat();

    private StairsPropFormat() {
        super(
                Stairs.Shape.class,
                Stairs.Shape::values,
                StairsProp::new
        );
    }

}
