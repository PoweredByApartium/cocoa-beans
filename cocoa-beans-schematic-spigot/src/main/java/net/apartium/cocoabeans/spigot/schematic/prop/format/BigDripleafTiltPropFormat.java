package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.BigDripleafTiltProp;
import org.bukkit.block.data.type.BigDripleaf;

public class BigDripleafTiltPropFormat extends EnumPropFormat<BigDripleaf.Tilt> {

    public static final BigDripleafTiltPropFormat INSTANCE = new BigDripleafTiltPropFormat();

    private BigDripleafTiltPropFormat() {
        super(
                BigDripleaf.Tilt.class,
                BigDripleaf.Tilt::values,
                BigDripleafTiltProp::new
        );
    }
}
