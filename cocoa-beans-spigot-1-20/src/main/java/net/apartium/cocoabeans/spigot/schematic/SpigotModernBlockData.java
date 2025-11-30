package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.spigot.schematic.prop.*;
import net.apartium.cocoabeans.spigot.schematic.prop.PowerablePoweredProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
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

        register(temp, Stairs.class, BlockProp.STAIRS_SHAPE, stairs -> new StairsProp(stairs.getShape()));
        register(temp, Bamboo.class, BlockProp.BAMBOO_LEAVES, bamboo -> new BambooProp(bamboo.getLeaves()));
        register(temp, Bed.class, BlockProp.BED_PART, bed -> new BedPartProp(bed.getPart()));
        register(temp, Beehive.class, BlockProp.BEEHIVE_HONEY_LEVEL, beehive -> new BeeHiveHoneyLevelProp(beehive.getHoneyLevel()));
        register(temp, Bell.class, BlockProp.BELL_ATTACHMENT, bell -> new BellAttachmentProp(bell.getAttachment()));
        register(temp, BigDripleaf.class, BlockProp.BIG_DRIP_LEAF_TILT, bigDripleaf -> new BigDripleafTiltProp(bigDripleaf.getTilt()));
        register(temp, BrewingStand.class, BlockProp.BREWING_STAND_BOTTLES, brewingStand -> new BrewingStandBottlesProp(brewingStand.getBottles().stream().mapToInt(value -> value).toArray()));
        register(temp, Cake.class, BlockProp.CAKE_BITES, cake -> new CakeBitesProp(cake.getBites()));
        register(temp, Campfire.class, BlockProp.CAMPFIRE_SIGNAL_FIRE, campfire -> new CampfireSignalFireProp(campfire.isSignalFire()));
        register(temp, Directional.class, BlockProp.DIRECTIONAL, directional -> new DirectionalFaceProp(directional.getFacing()));
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
        register(temp, Ageable.class, BlockProp.AGEABLE_AGE, ageable -> new AgeableAgeProp(ageable.getAge()));
        register(temp, AnaloguePowerable.class, BlockProp.ANALOGUE_POWERABLE_POWER, powerable -> new AnaloguePowerablePowerProp(powerable.getPower()));
        register(temp, Attachable.class, BlockProp.ATTACHABLE_ATTACHED, attachable -> new AttachableAttachedProp(attachable.isAttached()));
        register(temp, Bisected.class, BlockProp.BISECTED_HALF, bisected -> new BisectedHalfProp(bisected.getHalf()));
        register(temp, FaceAttachable.class, BlockProp.FACE_ATTACHABLE_ATTACHED_FACE, faceAttachable -> new FaceAttachableAttachedFaceProp(faceAttachable.getAttachedFace()));
        register(temp, Hangable.class, BlockProp.HANGABLE_HANGING, hangable -> new HangableHangingProp(hangable.isHanging()));
        register(temp, Levelled.class, BlockProp.LEVELLED_LEVEL, levelled -> new LevelledLevelProp(levelled.getLevel()));
        register(temp, Lightable.class, BlockProp.LIGHTABLE_LIT, lightable -> new LightableLitProp(lightable.isLit()));
        register(temp, MultipleFacing.class, BlockProp.MULTIPLE_FACING_FACES, multipleFacing -> new MultipleFacingFacesProp(multipleFacing.getFaces().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        multipleFacing::hasFace
                ))));
        register(temp, Openable.class, BlockProp.OPENABLE_OPEN, openable -> new OpenableOpenProp(openable.isOpen()));
        register(temp, Orientable.class, BlockProp.ORIENTABLE_AXIS, orientable -> new OrientableAxisProp(orientable.getAxis()));
        register(temp, Powerable.class, BlockProp.POWERABLE_POWERED, powerable -> new PowerablePoweredProp(powerable.isPowered()));
        register(temp, Rail.class, BlockProp.RAIL_SHAPE, rail -> new RailShapeProp(rail.getShape()));
        register(temp, Rotatable.class, BlockProp.ROTATABLE_ROTATION, rotatable -> new RotatableRotationProp(rotatable.getRotation()));
        register(temp, Snowable.class, BlockProp.SNOWABLE_SNOWY, snowable -> new SnowableSnowyProp(snowable.isSnowy()));
        register(temp, Waterlogged.class, BlockProp.WATERLOGGED, waterlogged -> new WaterloggedProp(waterlogged.isWaterlogged()));

        temp.replaceAll((k, v) -> List.copyOf(v));
        knownTypes = Map.copyOf(temp);
    }

    /* package-private */ static void add(BlockData blockData, Map<String, BlockProp<?>> props) {
        for (Map.Entry<Class<?>, List<BlockPropEntry>> entry : knownTypes.entrySet()) {
            Class<?> type = entry.getKey();

            if (!type.isAssignableFrom(blockData.getClass()))
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
