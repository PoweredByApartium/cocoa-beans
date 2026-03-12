package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.StructureBlockModeProp;
import org.bukkit.block.data.type.StructureBlock;

import java.util.function.Function;

public class StructureBlockModePropFormat extends EnumPropFormat<StructureBlock.Mode> {

    public static final StructureBlockModePropFormat INSTANCE = new StructureBlockModePropFormat();

    private StructureBlockModePropFormat() {
        this(StructureBlockModeProp::new);
    }

    public StructureBlockModePropFormat(Function<StructureBlock.Mode, BlockProp<StructureBlock.Mode>> constructor) {
        super(StructureBlock.Mode.class, StructureBlock.Mode::values, constructor);
    }

}
