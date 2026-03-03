package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.schematic.prop.LegacyDataProp;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpigotSchematicPlacer_1_8_R1Test {

    private final SpigotSchematicPlacer_1_8_R1 placer = new SpigotSchematicPlacer_1_8_R1();

    @Test
    void placeSetsAirWhenMaterialUnknown() {
        Block block = mock(Block.class);
        BlockData data = new GenericBlockData(
                new NamespacedKey("minecraft", "NOT_A_BLOCK"),
                Map.<String, BlockProp<?>>of()
        );
        BlockPlacement placement = new BlockPlacement(new Position(0, 0, 0), data);

        placer.place(block, placement);

        verify(block).setType(Material.AIR);
        verify(block, never()).setType(any(Material.class), eq(false));
        verify(block, never()).setData(anyByte());
        verify(block, never()).getState();
    }

    @Test
    void placeAppliesLegacyData() {
        Block block = mock(Block.class);
        BlockState state = mock(BlockState.class);
        when(block.getState()).thenReturn(state);

        Map<String, BlockProp<?>> props = new HashMap<>();
        props.put(BlockProp.Legacy.DATA, new LegacyDataProp((byte) 2));
        BlockData data = new GenericBlockData(new NamespacedKey("minecraft", "STONE"), props);
        BlockPlacement placement = new BlockPlacement(new Position(0, 0, 0), data);

        placer.place(block, placement);

        verify(block).setType(Material.STONE, false);
        verify(block).setData((byte) 2);
    }

    @Test
    void placeSetsSignLinesAndUpdates() {
        Block block = mock(Block.class);
        Sign sign = mock(Sign.class);
        when(block.getState()).thenReturn(sign);

        Map<String, BlockProp<?>> props = new HashMap<>();
        props.put(BlockProp.Legacy.DATA, new LegacyDataProp((byte) 1));
        props.put(BlockProp.Legacy.SIGN_LINES, (BlockProp<String[]>) () -> new String[] { "a", "b", "c", "d" });
        BlockData data = new GenericBlockData(new NamespacedKey("minecraft", "STONE"), props);
        BlockPlacement placement = new BlockPlacement(Position.ZERO, data);

        placer.place(block, placement);

        verify(block).setType(Material.STONE, false);
        verify(block).setData((byte) 1);
        verify(sign).setLine(0, "a");
        verify(sign).setLine(1, "b");
        verify(sign).setLine(2, "c");
        verify(sign).setLine(3, "d");
        verify(sign).update();
    }

    @Test
    void placeThrowsOnInvalidSignLines() {
        Block block = mock(Block.class);
        Sign sign = mock(Sign.class);
        when(block.getState()).thenReturn(sign);

        Map<String, BlockProp<?>> props = new HashMap<>();
        props.put(BlockProp.Legacy.DATA, new LegacyDataProp((byte) 1));
        props.put(BlockProp.Legacy.SIGN_LINES, (BlockProp<?>) () -> 5);
        BlockData data = new GenericBlockData(new NamespacedKey("minecraft", "STONE"), props);
        BlockPlacement placement = new BlockPlacement(new Position(0, 0, 0), data);

        assertThrows(IllegalArgumentException.class, () -> placer.place(block, placement));
    }

    @Test
    void getBlockDataReturnsNullForAir() {
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.AIR);

        assertNull(placer.getBlockData(block));
    }

    @Test
    void getBlockDataCollectsLegacyDataAndSignLines() {
        Block block = mock(Block.class);
        Sign sign = mock(Sign.class);
        when(block.getType()).thenReturn(Material.STONE);
        when(block.getData()).thenReturn((byte) 4);
        when(block.getState()).thenReturn(sign);
        when(sign.getLines()).thenReturn(new String[] { "l1", "l2", "l3", "l4" });

        BlockData data = placer.getBlockData(block);

        assertNotNull(data);
        assertEquals(new NamespacedKey("minecraft", "STONE"), data.type());

        BlockProp<?> legacy = data.props().get(BlockProp.Legacy.DATA);
        assertNotNull(legacy);
        assertEquals((byte) 4, legacy.value());

        BlockProp<?> lines = data.props().get(BlockProp.Legacy.SIGN_LINES);
        assertNotNull(lines);
        assertEquals(Arrays.asList("l1", "l2", "l3", "l4"), lines.value());
    }
}
