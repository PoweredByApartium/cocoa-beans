package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.RotatableProp;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.AvailableSince("0.0.46")
public record BedPartProp(Bed.Part value) implements BlockProp<Bed.Part>, SpigotPropHandler, RotatableProp<@NotNull BedPartProp> {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Bed bed))
            return;

        bed.setPart(value);
    }

    @Override
    public BedPartProp rotate(@NotNull NamespacedKey type, int degrees) {
        if (degrees == 180)
            return new BedPartProp(value == Bed.Part.HEAD ? Bed.Part.FOOT : Bed.Part.HEAD);

        return this;
    }
}
