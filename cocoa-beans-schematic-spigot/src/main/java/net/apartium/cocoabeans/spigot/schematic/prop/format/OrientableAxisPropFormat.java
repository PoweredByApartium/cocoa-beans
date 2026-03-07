package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.OrientableAxisProp;
import org.bukkit.Axis;

import java.util.function.Function;

public class OrientableAxisPropFormat extends EnumPropFormat<Axis> {

    public static final OrientableAxisPropFormat INSTANCE = new OrientableAxisPropFormat();

    private OrientableAxisPropFormat() {
        this(OrientableAxisProp::new);
    }

    public OrientableAxisPropFormat(Function<Axis, BlockProp<Axis>> constructor) {
        super(
                Axis.class,
                Axis::values,
                constructor
        );
    }

}
