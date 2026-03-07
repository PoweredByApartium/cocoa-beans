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
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public record DirectionalFaceProp(BlockFace value) implements BlockProp<BlockFace>, SpigotPropHandler, RotatableProp<DirectionalFaceProp>, FlippableProp<DirectionalFaceProp> {

    private static final List<BlockFace> ROTATION_16 = List.of(
            BlockFace.NORTH,
            BlockFace.NORTH_NORTH_EAST,
            BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST,
            BlockFace.EAST,
            BlockFace.EAST_SOUTH_EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_EAST,
            BlockFace.SOUTH,
            BlockFace.SOUTH_SOUTH_WEST,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.WEST_NORTH_WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH_NORTH_WEST
    );


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
        int index = ROTATION_16.indexOf(value);
        if (index == -1)
            return this;

        degrees = ((degrees % 360) + 360) % 360;
        if (degrees == 180)
            return new DirectionalFaceProp(value.getOppositeFace());

        int steps = switch (degrees) {
            case 90 -> 12;
            case 270 -> 4;
            default -> 0;
        };

        BlockFace rotated = ROTATION_16.get((index + steps) % ROTATION_16.size());

        return rotated == value
                ? this
                : new DirectionalFaceProp(rotated);
    }
}
