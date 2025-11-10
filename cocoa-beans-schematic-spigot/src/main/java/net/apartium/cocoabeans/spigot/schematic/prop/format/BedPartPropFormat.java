package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.BedPartProp;
import org.bukkit.block.data.type.Bed;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class BedPartPropFormat extends EnumPropFormat<Bed.Part> {

    public static final BedPartPropFormat INSTANCE = new BedPartPropFormat();

    private BedPartPropFormat() {
        super(
                Bed.Part.class,
                Bed.Part::values,
                BedPartProp::new
        );
    }
}
