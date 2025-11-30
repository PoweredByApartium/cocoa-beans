package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.RailShapeProp;
import org.bukkit.block.data.Rail;

import java.util.function.Function;

public class RailShapePropFormat extends EnumPropFormat<Rail.Shape> {

    public static final RailShapePropFormat INSTANCE = new RailShapePropFormat();

    private RailShapePropFormat() {
        this(RailShapeProp::new);
    }

    public RailShapePropFormat(Function<Rail.Shape, BlockProp<Rail.Shape>> constructor) {
        super(
                Rail.Shape.class,
                Rail.Shape::values,
                constructor
        );
    }
}
