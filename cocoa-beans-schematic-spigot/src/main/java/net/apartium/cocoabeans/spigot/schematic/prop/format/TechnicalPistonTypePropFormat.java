package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.TechnicalPistonTypeProp;
import org.bukkit.block.data.type.TechnicalPiston;

import java.util.function.Function;

public class TechnicalPistonTypePropFormat extends EnumPropFormat<TechnicalPiston.Type> {

    public static final TechnicalPistonTypePropFormat INSTANCE = new TechnicalPistonTypePropFormat();

    private TechnicalPistonTypePropFormat() {
        this(TechnicalPistonTypeProp::new);
    }

    public TechnicalPistonTypePropFormat(Function<TechnicalPiston.Type, BlockProp<TechnicalPiston.Type>> constructor) {
        super(TechnicalPiston.Type.class, TechnicalPiston.Type::values, constructor);
    }

}
