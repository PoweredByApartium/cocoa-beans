package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.Stairs;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpigotModernBlockDataTest {

    @Test
    void addIgnoresUnknownBlockData() {
        BlockData blockData = mock(BlockData.class);
        Map<String, BlockProp<?>> props = new HashMap<>();

        SpigotModernBlockData.add(blockData, props);

        assertTrue(props.isEmpty());
    }

    @Test
    void addCollectsStairsProps() {
        Stairs stairs = mock(Stairs.class);
        when(stairs.getShape()).thenReturn(Stairs.Shape.INNER_LEFT);
        when(stairs.getFacing()).thenReturn(BlockFace.EAST);
        when(stairs.getHalf()).thenReturn(Bisected.Half.TOP);
        when(stairs.isWaterlogged()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(stairs, props);

        assertPropEquals(props, BlockProp.STAIRS_SHAPE, Stairs.Shape.INNER_LEFT);
        assertPropEquals(props, BlockProp.DIRECTIONAL, BlockFace.EAST);
        assertPropEquals(props, BlockProp.BISECTED_HALF, Bisected.Half.TOP);
        assertPropEquals(props, BlockProp.WATERLOGGED, true);
    }

    @Test
    void addCollectsLeavesProps() {
        Leaves leaves = mock(Leaves.class);
        when(leaves.isPersistent()).thenReturn(true);
        when(leaves.getDistance()).thenReturn(7);
        when(leaves.isWaterlogged()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(leaves, props);

        assertPropEquals(props, BlockProp.LEAVES_PERSISTENT, true);
        assertPropEquals(props, BlockProp.LEAVES_DISTANCE, 7);
        assertPropEquals(props, BlockProp.WATERLOGGED, true);
    }

    @Test
    void addCollectsRepeaterProps() {
        Repeater repeater = mock(Repeater.class);
        when(repeater.getDelay()).thenReturn(4);
        when(repeater.isLocked()).thenReturn(true);
        when(repeater.getFacing()).thenReturn(BlockFace.SOUTH);
        when(repeater.isPowered()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(repeater, props);

        assertPropEquals(props, BlockProp.REPEATER_DELAY, 4);
        assertPropEquals(props, BlockProp.REPEATER_LOCKED, true);
        assertPropEquals(props, BlockProp.DIRECTIONAL, BlockFace.SOUTH);
        assertPropEquals(props, BlockProp.POWERABLE_POWERED, true);
    }

    private static void assertPropEquals(Map<String, BlockProp<?>> props, String key, Object expected) {
        BlockProp<?> prop = props.get(key);
        assertNotNull(prop, "Missing prop " + key);
        assertEquals(expected, prop.value());
    }
}
