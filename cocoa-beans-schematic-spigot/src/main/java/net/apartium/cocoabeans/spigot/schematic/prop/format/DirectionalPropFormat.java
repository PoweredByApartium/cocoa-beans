package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.DirectionalFaceProp;
import org.bukkit.block.BlockFace;

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
