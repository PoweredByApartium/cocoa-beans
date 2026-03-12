package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
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

    @Test
    void addCollectsAgeableProps() {
        Ageable ageable = mock(Ageable.class);
        when(ageable.getAge()).thenReturn(5);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(ageable, props);

        assertPropEquals(props, BlockProp.AGEABLE_AGE, 5);
    }

    @Test
    void addCollectsOpenableProps() {
        Openable openable = mock(Openable.class);
        when(openable.isOpen()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(openable, props);

        assertPropEquals(props, BlockProp.OPENABLE_OPEN, true);
    }

    @Test
    void addCollectsOrientableProps() {
        Orientable orientable = mock(Orientable.class);
        when(orientable.getAxis()).thenReturn(Axis.Y);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(orientable, props);

        assertPropEquals(props, BlockProp.ORIENTABLE_AXIS, Axis.Y);
    }

    @Test
    void addCollectsWaterloggedProps() {
        Waterlogged waterlogged = mock(Waterlogged.class);
        when(waterlogged.isWaterlogged()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(waterlogged, props);

        assertPropEquals(props, BlockProp.WATERLOGGED, true);
    }

    @Test
    void addCollectsPowerableProps() {
        Powerable powerable = mock(Powerable.class);
        when(powerable.isPowered()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(powerable, props);

        assertPropEquals(props, BlockProp.POWERABLE_POWERED, true);
    }

    @Test
    void addCollectsAnaloguePowerableProps() {
        AnaloguePowerable powerable = mock(AnaloguePowerable.class);
        when(powerable.getPower()).thenReturn(12);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(powerable, props);

        assertPropEquals(props, BlockProp.ANALOGUE_POWERABLE_POWER, 12);
    }

    @Test
    void addCollectsLightableProps() {
        Lightable lightable = mock(Lightable.class);
        when(lightable.isLit()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(lightable, props);

        assertPropEquals(props, BlockProp.LIGHTABLE_LIT, true);
    }

    @Test
    void addCollectsLevelledProps() {
        Levelled levelled = mock(Levelled.class);
        when(levelled.getLevel()).thenReturn(7);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(levelled, props);

        assertPropEquals(props, BlockProp.LEVELLED_LEVEL, 7);
    }

    @Test
    void addCollectsBisectedHalfProps() {
        Bisected bisected = mock(Bisected.class);
        when(bisected.getHalf()).thenReturn(Bisected.Half.BOTTOM);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(bisected, props);

        assertPropEquals(props, BlockProp.BISECTED_HALF, Bisected.Half.BOTTOM);
    }

    @Test
    void addCollectsDirectionalProps() {
        Directional directional = mock(Directional.class);
        when(directional.getFacing()).thenReturn(BlockFace.NORTH);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(directional, props);

        assertPropEquals(props, BlockProp.DIRECTIONAL, BlockFace.NORTH);
    }

    @Test
    void addCollectsHangableProps() {
        Hangable hangable = mock(Hangable.class);
        when(hangable.isHanging()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(hangable, props);

        assertPropEquals(props, BlockProp.HANGABLE_HANGING, true);
    }

    @Test
    void addCollectsSnowableProps() {
        Snowable snowable = mock(Snowable.class);
        when(snowable.isSnowy()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(snowable, props);

        assertPropEquals(props, BlockProp.SNOWABLE_SNOWY, true);
    }

    @Test
    void addCollectsAttachableProps() {
        Attachable attachable = mock(Attachable.class);
        when(attachable.isAttached()).thenReturn(true);

        Map<String, BlockProp<?>> props = new HashMap<>();
        SpigotModernBlockData.add(attachable, props);

        assertPropEquals(props, BlockProp.ATTACHABLE_ATTACHED, true);
    }

    private static void assertPropEquals(Map<String, BlockProp<?>> props, String key, Object expected) {
        BlockProp<?> prop = props.get(key);
        assertNotNull(prop, "Missing prop " + key);
        assertEquals(expected, prop.value());
    }
}
