package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.spigot.schematic.prop.SpigotPropHandler;
import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.spigot.SpigotTestBase;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class SpigotSchematicPlacer_1_20_R1Test extends SpigotTestBase {

    private SpigotSchematicPlacer_1_20_R1 placer;

    @Override
    public void initialize() {
        placer = new SpigotSchematicPlacer_1_20_R1();
    }

    @Test
    void placeSkipsWhenMaterialUnknown() {
        Block block = mock(Block.class);
        BlockData data = new GenericBlockData(
                new NamespacedKey("minecraft", "NOT_A_BLOCK"),
                Map.of()
        );
        BlockPlacement placement = new BlockPlacement(Position.ZERO, data);

        placer.place(block, placement);

        verify(block, never()).setBlockData(any());
    }

    @Test
    void placeUpdatesSpigotPropsAndSetsBlockData() {
        Block block = mock(Block.class);
        SpigotPropHandler handler = mock(SpigotPropHandler.class, withSettings().extraInterfaces(BlockProp.class));

        BlockData data = new GenericBlockData(
                new NamespacedKey("minecraft", "stone"),
                Map.of("CUSTOM", (BlockProp<?>) handler)
        );
        BlockPlacement placement = new BlockPlacement(Position.ZERO, data);

        placer.place(block, placement);

        ArgumentCaptor<org.bukkit.block.data.BlockData> captor =
                ArgumentCaptor.forClass(org.bukkit.block.data.BlockData.class);
        verify(handler).update(captor.capture());
        verify(block).setBlockData(captor.getValue());
    }

    @Test
    void getBlockDataReturnsTypeAndEmptyPropsForUnknownBlockData() {
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.STONE);
        when(block.getBlockData()).thenReturn(mock(org.bukkit.block.data.BlockData.class));

        BlockData data = placer.getBlockData(block);

        assertNotNull(data);
        assertEquals(new NamespacedKey("minecraft", "stone"), data.type());
        assertTrue(data.props().isEmpty());
    }
}
