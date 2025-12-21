package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.FlippableProp;
import net.apartium.cocoabeans.schematic.prop.RotatableProp;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public record DirectionalFaceProp(BlockFace value) implements BlockProp<BlockFace>, SpigotPropHandler, RotatableProp<@NotNull DirectionalFaceProp>, FlippableProp<DirectionalFaceProp> {
    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Directional directional))
            return;

        directional.setFacing(value);
    }

    @Override
    public DirectionalFaceProp flip(NamespacedKey type, Axis axis) {
        return switch (axis) {
            case X -> {
                if (value.name().contains("EAST") || value.name().contains("WEST"))
                    yield new DirectionalFaceProp(value.getOppositeFace());
                else
                    yield this;
            }

            case Y -> {
                if (value == BlockFace.DOWN || value == BlockFace.UP)
                    yield new DirectionalFaceProp(value.getOppositeFace());
                else
                    yield this;
            }

            case Z -> {
                if (value.name().contains("SOUTH") || value.name().contains("NORTH"))
                    yield new DirectionalFaceProp(value.getOppositeFace());
                else
                    yield this;
            }
        };
    }

    @Override
    public DirectionalFaceProp rotate(NamespacedKey type, int degrees) {
        // TODO impl this
        return this;
    }
}
