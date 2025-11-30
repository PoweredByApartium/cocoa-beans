package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.spigot.schematic.prop.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.*;
import org.bukkit.block.data.type.Comparator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/* package-private */ class SpigotModernBlockData {

    private static final Map<Class<?>, List<BlockPropEntry>>
            knownTypes;

    static {
        Map<Class<?>, List<BlockPropEntry>> temp = new HashMap<>();
        register(temp, Cake.class, BlockProp.CAKE_BITES, cake -> new CakeBitesProp(cake.getBites()));
        register(temp, Campfire.class, BlockProp.CAMPFIRE_SIGNAL_FIRE, campfire -> new CampfireSignalFireProp(campfire.isSignalFire()));

        register(temp, BubbleColumn.class, BlockProp.BUBBLE_COLUMN_DRAG, bubbleColumn -> new BubbleColumnProp(bubbleColumn.isDrag()));
        register(temp, Candle.class, BlockProp.CANDLE_CANDLES, candle -> new CandleProp(candle.getCandles()));
        register(temp, CaveVinesPlant.class, BlockProp.CAVE_VINES_PLANT_BERRIES, caveVinesPlant -> new CaveVinesPlantBerriesProp(caveVinesPlant.isBerries()));
        register(temp, Chest.class, BlockProp.CHEST_TYPE, chest -> new ChestTypeProp(chest.getType()));
        register(temp, CommandBlock.class, BlockProp.COMMAND_BLOCK_CONDITIONAL, commandBlock -> new CommandBlockConditionalProp(commandBlock.isConditional()));
        register(temp, Comparator.class, BlockProp.COMPARATOR_MODE, comparator -> new ComparatorModeProp(comparator.getMode()));
        register(temp, DaylightDetector.class, BlockProp.DAY_LIGHT_DETECTOR_INVERTED, daylightDetector -> new DayLightDetectorInvertedProp(daylightDetector.isInverted()));
        register(temp, Dispenser.class, BlockProp.DISPENSER_TRIGGERED, dispenser -> new DispenserTriggeredProp(dispenser.isTriggered()));
        register(temp, Door.class, BlockProp.DOOR_HINGE, door -> new DoorHingeProp(door.getHinge()));
        register(temp, EndPortalFrame.class, BlockProp.END_PORTAL_FRAME_EYE, endPortalFrame -> new EndPortalFrameEyeProp(endPortalFrame.hasEye()));
        register(temp, Farmland.class, BlockProp.FARM_LAND_MOISTURE, farmland -> new FarmLandMoistureProp(farmland.getMoisture()));
        register(temp, Gate.class, BlockProp.GATE_IN_WALL, gate -> new GateInWallProp(gate.isInWall()));
        register(temp, Hopper.class, BlockProp.HOPPER_ENABLED, hopper -> new HopperEnabledProp(hopper.isEnabled()));
        register(temp, Jigsaw.class, BlockProp.JIGSAW_ORIENTATION, jigsaw -> new JigsawOrientationProp(jigsaw.getOrientation()));
        register(temp, Leaves.class, BlockProp.LEAVES_PERSISTENT, leaves -> new LeavesPersistentProp(leaves.isPersistent()));
        register(temp, Leaves.class, BlockProp.LEAVES_DISTANCE, leaves -> new LeavesDistanceProp(leaves.getDistance()));
        register(temp, NoteBlock.class, BlockProp.NOTE_BLOCK_INSTRUMENT, noteBlock -> new NoteBlockInstrumentProp(noteBlock.getInstrument()));
        register(temp, NoteBlock.class, BlockProp.NOTE_BLOCK_NOTE, noteBlock -> new NoteBlockNoteProp(noteBlock.getNote()));
        register(temp, Piston.class, BlockProp.PISTON_EXTENDED, piston -> new PistonExtendedProp(piston.isExtended()));
        register(temp, PistonHead.class, BlockProp.PISTON_HEAD_IS_SHORT, pistonHead -> new PistonHeadIsShortProp(pistonHead.isShort()));
        register(temp, PointedDripstone.class, BlockProp.POINTED_DRIPSTONE_VERTICAL_DIRECTION, pointedDripstone -> new PointedDripstoneVerticalDirectionProp(pointedDripstone.getVerticalDirection()));
        register(temp, PointedDripstone.class, BlockProp.POINTED_DRIPSTONE_THICKNESS, pointedDripstone -> new PointedDripstoneThicknessProp(pointedDripstone.getThickness()));
        register(temp, RedstoneWire.class, BlockProp.REDSTONE_WIRE_CONNECTIONS, wire -> new RedstoneWireConnectionsProp(
                wire.getAllowedFaces().stream()
                        .map(face -> Map.entry(
                                face,
                                wire.getFace(face)
                        ))
                        .filter(entry -> entry.getValue() != RedstoneWire.Connection.NONE)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
        register(temp, Repeater.class, BlockProp.REPEATER_DELAY, repeater -> new RepeaterDelayProp(repeater.getDelay()));
        register(temp, Repeater.class, BlockProp.REPEATER_LOCKED, repeater -> new RepeaterLockedProp(repeater.isLocked()));
        register(temp, RespawnAnchor.class, BlockProp.RESPAWN_ANCHOR_CHARGES, anchor -> new RespawnAnchorChargesProp(anchor.getCharges()));
        register(temp, Sapling.class, BlockProp.SAPLING_STAGE, sapling -> new SaplingStageProp(sapling.getStage()));
        register(temp, Scaffolding.class, BlockProp.SCAFFOLDING_BOTTOM, scaffolding -> new ScaffoldingBottomProp(scaffolding.isBottom()));
        register(temp, Scaffolding.class, BlockProp.SCAFFOLDING_DISTANCE, scaffolding -> new ScaffoldingDistanceProp(scaffolding.getDistance()));
        register(temp, SculkCatalyst.class, BlockProp.SCULK_CATALYST_BLOOM, sculkCatalyst -> new SculkCatalystBloomProp(sculkCatalyst.isBloom()));
        register(temp, SculkSensor.class, BlockProp.SCULK_SENSOR_PHASE, sculkSensor -> new SculkSensorPhaseProp(sculkSensor.getPhase()));
        register(temp, SculkShrieker.class, BlockProp.SCULK_SHRIEKER_CAN_SUMMON, sculkShrieker -> new SculkShriekerCanSummonProp(sculkShrieker.isCanSummon()));
        register(temp, SculkShrieker.class, BlockProp.SCULK_SHRIEKER_SHRIEKING, sculkShrieker -> new SculkShriekerShrinkingProp(sculkShrieker.isShrieking()));
        register(temp, SeaPickle.class, BlockProp.SEA_PICKLE_PICKLES, seaPickle -> new SeaPicklePicklesProp(seaPickle.getPickles()));
        register(temp, Slab.class, BlockProp.SLAB_TYPE, slab -> new SlabTypeProp(slab.getType()));
        register(temp, Snow.class, BlockProp.SNOW_LAYERS, snow -> new SnowLayersProp(snow.getLayers()));
        register(temp, StructureBlock.class, BlockProp.STRUCTURE_BLOCK_MODE, structureBlock -> new StructureBlockModeProp(structureBlock.getMode()));
        register(temp, TechnicalPiston.class, BlockProp.TECHNICAL_PISTON_TYPE, technicalPiston -> new TechnicalPistonTypeProp(technicalPiston.getType()));
        register(temp, TNT.class, BlockProp.TNT_UNSTABLE, tnt -> new TNTUnstableProp(tnt.isUnstable()));
        register(temp, Tripwire.class, BlockProp.TRIPWIRE_DISARMED, tripwire -> new TripwireDisarmedProp(tripwire.isDisarmed()));
        register(temp, TurtleEgg.class, BlockProp.TURTLE_EGG_EGGS, turtleEgg -> new TurtleEggEggsProp(turtleEgg.getEggs()));
        register(temp, TurtleEgg.class, BlockProp.TURTLE_EGG_HATCH, turtleEgg -> new TurtleEggHatchProp(turtleEgg.getHatch()));
        register(temp, Wall.class, BlockProp.WALL_UP, wall -> new WallUpProp(wall.isUp()));
        register(temp, Wall.class, BlockProp.WALL_HEIGHTS, wall -> {
            final BlockFace[] DIRECTIONS = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

            return new WallHeightsProp(
                    Arrays.stream(DIRECTIONS)
                            .map(face -> Map.entry(
                                    face,
                                    wall.getHeight(face)
                            ))
                            .filter(entry -> entry.getValue() != Wall.Height.NONE)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        });

        temp.replaceAll((k, v) -> List.copyOf(v));
        knownTypes = Map.copyOf(temp);
    }

    /* package-private */ static void add(BlockData blockData, Map<String, BlockProp<?>> props) {
        for (Map.Entry<Class<?>, List<BlockPropEntry>> entry : knownTypes.entrySet()) {
            Class<?> type = entry.getKey();

            if (type.isAssignableFrom(blockData.getClass()))
                continue;

            for (BlockPropEntry blockPropEntry : entry.getValue()) {
                BlockProp<?> result = blockPropEntry.function.apply(blockData);
                props.put(blockPropEntry.key, result);
            }

        }
    }

    record BlockPropEntry(Function<BlockData, BlockProp<?>> function, String key) {}

    @SuppressWarnings("unchecked")
    private static <T extends BlockData> void register(Map<Class<?>, List<BlockPropEntry>> map, Class<T> type, String key, Function<T, BlockProp<?>> function) {
        List<BlockPropEntry> blockPropEntries = map.computeIfAbsent(type, k -> new ArrayList<>());
        blockPropEntries.add(new BlockPropEntry(data -> function.apply((T) data), key));
    }
}
