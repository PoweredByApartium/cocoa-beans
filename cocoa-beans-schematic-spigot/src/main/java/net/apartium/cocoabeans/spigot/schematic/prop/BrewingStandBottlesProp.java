package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BrewingStand;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Objects;

@ApiStatus.AvailableSince("0.0.46")
public record BrewingStandBottlesProp(int[] value) implements BlockProp<int[]>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof BrewingStand brewingStand))
            return;

        for (int bottle : value)
            brewingStand.setBottle(bottle, true);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BrewingStandBottlesProp that = (BrewingStandBottlesProp) o;
        return Objects.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "BrewingStandBottlesProp{" +
                "value=" + Arrays.toString(value) +
                '}';
    }
}
