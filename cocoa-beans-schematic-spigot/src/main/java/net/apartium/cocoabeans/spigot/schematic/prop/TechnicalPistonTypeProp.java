package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TechnicalPiston;

public record TechnicalPistonTypeProp(TechnicalPiston.Type type) implements BlockProp<TechnicalPiston.Type>, SpigotPropHandler {

    @Override
    public TechnicalPiston.Type value() {
        return type;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof TechnicalPiston technicalPiston))
            return;

        technicalPiston.setType(type);
    }

}
