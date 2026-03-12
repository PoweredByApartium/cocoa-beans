package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.PasteResult;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpigotPasteOperationTest extends SpigotTestBase {

    private static BlockData stone() {
        return new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
    }

    private static BlockData dirt() {
        return new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
    }

    private static BlockIterator singleBlock(BlockPlacement placement) {
        return new BlockIterator() {
            boolean done = false;

            @Override public boolean hasNext() { return !done; }

            @Override
            public BlockPlacement next() {
                done = true;
                return placement;
            }

            @Override
            public Position current() { return done ? placement.position() : null; }
        };
    }

    private SpigotPasteOperation op(Location origin, BlockIterator iter, SpigotSchematicPlacer placer) {
        return new SpigotPasteOperation(
                origin, iter, AxisOrder.XYZ,
                (b, p) -> true,
                bp -> bp.block(),
                placer
        );
    }

    @Test
    void origin_returnsCloneNotSameReference() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotPasteOperation operation = op(origin, singleBlock(new BlockPlacement(new Position(0, 0, 0), stone())), mock(SpigotSchematicPlacer.class));

        assertNotSame(origin, operation.origin());
    }

    @Test
    void origin_cloneHasSameCoordinates() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 3, 64, 7);
        SpigotPasteOperation operation = op(origin, singleBlock(new BlockPlacement(new Position(0, 0, 0), stone())), mock(SpigotSchematicPlacer.class));

        Location returned = operation.origin();
        assertEquals(origin.getX(), returned.getX());
        assertEquals(origin.getY(), returned.getY());
        assertEquals(origin.getZ(), returned.getZ());
    }

    @Test
    void setShouldPlace_returnsSelf() {
        World world = server.addSimpleWorld("test");
        SpigotPasteOperation operation = op(new Location(world, 0, 64, 0), singleBlock(new BlockPlacement(new Position(0, 0, 0), stone())), mock(SpigotSchematicPlacer.class));

        assertSame(operation, operation.setShouldPlace((b, p) -> true));
    }

    @Test
    void setMapper_returnsSelf() {
        World world = server.addSimpleWorld("test");
        SpigotPasteOperation operation = op(new Location(world, 0, 64, 0), singleBlock(new BlockPlacement(new Position(0, 0, 0), stone())), mock(SpigotSchematicPlacer.class));

        assertSame(operation, operation.setMapper(bp -> bp.block()));
    }

    @Test
    void addPostPlaceAction_returnsSelf() {
        World world = server.addSimpleWorld("test");
        SpigotPasteOperation operation = op(new Location(world, 0, 64, 0), singleBlock(new BlockPlacement(new Position(0, 0, 0), stone())), mock(SpigotSchematicPlacer.class));

        assertSame(operation, operation.addPostPlaceAction((b, d) -> {}));
    }

    @Test
    void performAll_whenShouldPlaceReturnsFalse_blockIsNotCounted() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), stone());

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> false,
                p -> p.block(),
                placer
        );

        PasteResult result = operation.performAll();

        assertEquals(0, result.blockPlaces());
        verify(placer, never()).place(any(), any());
    }

    @Test
    void performAll_whenShouldPlaceReturnsTrue_placerIsCalled() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), stone());

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> true,
                p -> p.block(),
                placer
        );

        PasteResult result = operation.performAll();

        assertEquals(1, result.blockPlaces());
        verify(placer, times(1)).place(any(Block.class), any(BlockPlacement.class));
    }

    @Test
    void performAll_mapperTransformsBlock_transformedDataIsPassedToPlacer() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockData originalData = stone();
        BlockData mappedData = dirt();
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), originalData);

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> true,
                p -> mappedData,
                placer
        );

        operation.performAll();

        ArgumentCaptor<BlockPlacement> captor = ArgumentCaptor.forClass(BlockPlacement.class);
        verify(placer).place(any(Block.class), captor.capture());
        assertSame(mappedData, captor.getValue().block());
    }

    @Test
    void performAll_mapperReturnsSameBlock_originalPlacementPassedToPlacer() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockData data = stone();
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), data);

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> true,
                p -> p.block(),
                placer
        );

        operation.performAll();

        ArgumentCaptor<BlockPlacement> captor = ArgumentCaptor.forClass(BlockPlacement.class);
        verify(placer).place(any(Block.class), captor.capture());
        assertSame(data, captor.getValue().block());
    }

    @Test
    void performAll_postPlaceActionsAreInvoked() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockData data = stone();
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), data);

        List<Block> seenBlocks = new ArrayList<>();
        List<BlockData> seenData = new ArrayList<>();

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> true,
                p -> p.block(),
                placer
        );

        operation.addPostPlaceAction((block, d) -> {
            seenBlocks.add(block);
            seenData.add(d);
        });

        operation.performAll();

        assertEquals(1, seenBlocks.size());
        assertSame(data, seenData.get(0));
    }

    @Test
    void performAll_postPlaceActionsNotInvokedWhenShouldPlaceFalse() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), stone());

        List<Block> seenBlocks = new ArrayList<>();

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> false,
                p -> p.block(),
                placer
        );

        operation.addPostPlaceAction((block, d) -> seenBlocks.add(block));

        operation.performAll();

        assertTrue(seenBlocks.isEmpty());
    }

    @Test
    void performAll_multiplePostPlaceActionsAllInvoked() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), stone());

        List<Integer> invocations = new ArrayList<>();

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> true,
                p -> p.block(),
                placer
        );

        operation.addPostPlaceAction((block, d) -> invocations.add(1));
        operation.addPostPlaceAction((block, d) -> invocations.add(2));
        operation.addPostPlaceAction((block, d) -> invocations.add(3));

        operation.performAll();

        assertEquals(List.of(1, 2, 3), invocations);
    }

    @Test
    void setShouldPlace_replacesExistingFilter() {
        World world = server.addSimpleWorld("test");
        Location origin = new Location(world, 0, 64, 0);
        SpigotSchematicPlacer placer = mock(SpigotSchematicPlacer.class);
        BlockPlacement bp = new BlockPlacement(new Position(0, 0, 0), stone());

        SpigotPasteOperation operation = new SpigotPasteOperation(
                origin, singleBlock(bp), AxisOrder.XYZ,
                (b, p) -> true,
                p -> p.block(),
                placer
        );

        operation.setShouldPlace((b, p) -> false);

        PasteResult result = operation.performAll();
        assertEquals(0, result.blockPlaces());
        verify(placer, never()).place(any(), any());
    }
}
