package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.ChestTypeProp;
import org.bukkit.block.data.type.Chest;

public class ChestTypePropFormat extends EnumPropFormat<Chest.Type> {

    public static final ChestTypePropFormat INSTANCE = new ChestTypePropFormat();

    private ChestTypePropFormat() {
        super(
                Chest.Type.class,
                Chest.Type::values,
                ChestTypeProp::new
        );
    }

}
