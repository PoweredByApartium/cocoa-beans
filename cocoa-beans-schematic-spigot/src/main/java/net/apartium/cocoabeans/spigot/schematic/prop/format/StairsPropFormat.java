package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.StairsProp;
import org.bukkit.block.data.type.Stairs;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
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
