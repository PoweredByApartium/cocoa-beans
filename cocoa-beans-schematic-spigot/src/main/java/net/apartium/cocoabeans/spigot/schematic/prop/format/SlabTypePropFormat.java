package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.SlabTypeProp;
import org.bukkit.block.data.type.Slab;

import java.util.function.Function;

public class SlabTypePropFormat extends EnumPropFormat<Slab.Type> {

    public static final SlabTypePropFormat INSTANCE = new SlabTypePropFormat();

    private SlabTypePropFormat() {
        this(SlabTypeProp::new);
    }

    public SlabTypePropFormat(Function<Slab.Type, BlockProp<Slab.Type>> constructor) {
        super(Slab.Type.class, Slab.Type::values, constructor);
    }
}
