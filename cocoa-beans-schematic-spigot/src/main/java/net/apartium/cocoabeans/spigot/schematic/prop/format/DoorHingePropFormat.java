package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.DoorHingeProp;
import org.bukkit.block.data.type.Door;

public class DoorHingePropFormat extends EnumPropFormat<Door.Hinge> {

    public static final DoorHingePropFormat INSTANCE = new DoorHingePropFormat();

    private DoorHingePropFormat() {
        super(
                Door.Hinge.class,
                Door.Hinge::values,
                DoorHingeProp::new
        );
    }

}
