package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.DirectionalFaceProp;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class DirectionalPropFormat extends EnumPropFormat<BlockFace> {

    public static final DirectionalPropFormat INSTANCE = new DirectionalPropFormat();

    private DirectionalPropFormat() {
        super(
                BlockFace.class,
                BlockFace::values,
                DirectionalFaceProp::new
        );
    }

}
