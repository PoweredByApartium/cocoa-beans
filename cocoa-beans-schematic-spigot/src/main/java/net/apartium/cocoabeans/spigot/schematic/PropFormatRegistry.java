package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.*;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.schematic.prop.*;
import net.apartium.cocoabeans.spigot.schematic.prop.format.*;
import net.apartium.cocoabeans.structs.MinecraftVersion;

import java.util.HashMap;
import java.util.Map;

public class PropFormatRegistry {

    private PropFormatRegistry() { }

    private static final Map<String, BlockPropFormat<?>> FORMATS;

    static {
        MinecraftVersion version = ServerUtils.getVersion();

        Map<String, BlockPropFormat<?>> propFormatMap = new HashMap<>();

        if (version.isLowerThanOrEqual(MinecraftVersion.V1_12_2)) {
            propFormatMap.put(BlockProp.Legacy.DATA, new ByteBlockPropFormat(LegacyDataProp::new));
            propFormatMap.put(BlockProp.Legacy.SIGN_LINES, ListStringBlockPropFormat.INSTANCE);
        } else {
            propFormatMap.put(BlockProp.STAIRS_SHAPE, StairsPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.DIRECTIONAL, DirectionalPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BAMBOO_LEAVES, BambooPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BED_PART, BedPartPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BEEHIVE_HONEY_LEVEL, new IntPropFormat(BeeHiveHoneyLevelProp::new));
            propFormatMap.put(BlockProp.BELL_ATTACHMENT, BellAttachmentPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BIG_DRIP_LEAF_TILT, BigDripleafTiltPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.BREWING_STAND_BOTTLES, new IntArrayPropFormat(BrewingStandBottlesProp::new));
            propFormatMap.put(BlockProp.CAKE_BITES, new IntPropFormat(CakeBitesProp::new));
            propFormatMap.put(BlockProp.CAMPFIRE_SIGNAL_FIRE, new BooleanPropFormat(CampfireSignalFireProp::new));
            propFormatMap.put(BlockProp.BUBBLE_COLUMN_DRAG, new BooleanPropFormat(BubbleColumnProp::new));
            propFormatMap.put(BlockProp.CANDLE_CANDLES, new IntPropFormat(CandleProp::new));
            propFormatMap.put(BlockProp.CAVE_VINES_PLANT_BERRIES, new BooleanPropFormat(CaveVinesPlantBerriesProp::new));
            propFormatMap.put(BlockProp.CHEST_TYPE, ChestTypePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.COMMAND_BLOCK_CONDITIONAL, new BooleanPropFormat(CommandBlockConditionalProp::new));
            propFormatMap.put(BlockProp.COMPARATOR_MODE, ComparatorModePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.DAY_LIGHT_DETECTOR_INVERTED, new BooleanPropFormat(DayLightDetectorInvertedProp::new));
            propFormatMap.put(BlockProp.DISPENSER_TRIGGERED, new BooleanPropFormat(DispenserTriggeredProp::new));
            propFormatMap.put(BlockProp.DOOR_HINGE, DoorHingePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.END_PORTAL_FRAME_EYE, new BooleanPropFormat(EndPortalFrameEyeProp::new));
            propFormatMap.put(BlockProp.FARM_LAND_MOISTURE, new IntPropFormat(FarmLandMoistureProp::new));
            propFormatMap.put(BlockProp.GATE_IN_WALL, new BooleanPropFormat(GateInWallProp::new));
            propFormatMap.put(BlockProp.HOPPER_ENABLED, new BooleanPropFormat(HopperEnabledProp::new));
            propFormatMap.put(BlockProp.JIGSAW_ORIENTATION, JigsawOrientationPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.LEAVES_PERSISTENT, new BooleanPropFormat(LeavesPersistentProp::new));
            propFormatMap.put(BlockProp.LEAVES_DISTANCE, new IntPropFormat(LeavesDistanceProp::new));
            propFormatMap.put(BlockProp.NOTE_BLOCK_INSTRUMENT, NoteBlockInstrumentPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.NOTE_BLOCK_NOTE, NoteBlockNotePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.PISTON_EXTENDED, new BooleanPropFormat(PistonExtendedProp::new));
            propFormatMap.put(BlockProp.PISTON_HEAD_IS_SHORT, new BooleanPropFormat(PistonHeadIsShortProp::new));
            propFormatMap.put(BlockProp.POINTED_DRIPSTONE_VERTICAL_DIRECTION, new BlockFacePropFormat(PointedDripstoneVerticalDirectionProp::new));
            propFormatMap.put(BlockProp.POINTED_DRIPSTONE_THICKNESS, PointedDripstoneThicknessPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.REDSTONE_WIRE_CONNECTIONS, RedstoneWireConnectionsPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.REPEATER_DELAY, new IntPropFormat(RepeaterDelayProp::new));
            propFormatMap.put(BlockProp.REPEATER_LOCKED, new BooleanPropFormat(RepeaterLockedProp::new));
            propFormatMap.put(BlockProp.RESPAWN_ANCHOR_CHARGES, new IntPropFormat(RespawnAnchorChargesProp::new));
            propFormatMap.put(BlockProp.SAPLING_STAGE, new IntPropFormat(SaplingStageProp::new));
            propFormatMap.put(BlockProp.SCAFFOLDING_BOTTOM, new BooleanPropFormat(ScaffoldingBottomProp::new));
            propFormatMap.put(BlockProp.SCAFFOLDING_DISTANCE, new IntPropFormat(ScaffoldingDistanceProp::new));
            propFormatMap.put(BlockProp.SCULK_CATALYST_BLOOM, new BooleanPropFormat(SculkCatalystBloomProp::new));
            propFormatMap.put(BlockProp.SCULK_SENSOR_PHASE, SculkSensorPhasePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.SCULK_SHRIEKER_CAN_SUMMON, new BooleanPropFormat(SculkShriekerCanSummonProp::new));
            propFormatMap.put(BlockProp.SCULK_SHRIEKER_SHRIEKING, new BooleanPropFormat(SculkShriekerShrinkingProp::new));
            propFormatMap.put(BlockProp.SEA_PICKLE_PICKLES, new IntPropFormat(SeaPicklePicklesProp::new));
            propFormatMap.put(BlockProp.SLAB_TYPE, SlabTypePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.SNOW_LAYERS, new IntPropFormat(SnowLayersProp::new));
            propFormatMap.put(BlockProp.STRUCTURE_BLOCK_MODE, StructureBlockModePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.TECHNICAL_PISTON_TYPE, TechnicalPistonTypePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.TNT_UNSTABLE, new BooleanPropFormat(TNTUnstableProp::new));
            propFormatMap.put(BlockProp.TRIPWIRE_DISARMED, new BooleanPropFormat(TripwireDisarmedProp::new));
            propFormatMap.put(BlockProp.TURTLE_EGG_EGGS, new IntPropFormat(TurtleEggEggsProp::new));
            propFormatMap.put(BlockProp.TURTLE_EGG_HATCH, new IntPropFormat(TurtleEggHatchProp::new));
            propFormatMap.put(BlockProp.WALL_UP, new BooleanPropFormat(WallUpProp::new));
            propFormatMap.put(BlockProp.WALL_HEIGHTS, WallHeightsPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.AGEABLE_AGE, new IntPropFormat(AgeableAgeProp::new));
            propFormatMap.put(BlockProp.ANALOGUE_POWERABLE_POWER, new IntPropFormat(AnaloguePowerablePowerProp::new));
            propFormatMap.put(BlockProp.ATTACHABLE_ATTACHED, new BooleanPropFormat(AttachableAttachedProp::new));
            propFormatMap.put(BlockProp.BISECTED_HALF, BisectedHalfPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.FACE_ATTACHABLE_ATTACHED_FACE, FaceAttachableAttachedFacePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.HANGABLE_HANGING, new BooleanPropFormat(HangableHangingProp::new));
            propFormatMap.put(BlockProp.LEVELLED_LEVEL, new IntPropFormat(LevelledLevelProp::new));
            propFormatMap.put(BlockProp.LIGHTABLE_LIT, new BooleanPropFormat(LightableLitProp::new));
            propFormatMap.put(BlockProp.MULTIPLE_FACING_FACES, MultipleFacingFacesPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.OPENABLE_OPEN, new BooleanPropFormat(OpenableOpenProp::new));
            propFormatMap.put(BlockProp.ORIENTABLE_AXIS, OrientableAxisPropFormat.INSTANCE);
            propFormatMap.put(BlockProp.POWERABLE_POWERED, new BooleanPropFormat(PowerablePoweredProp::new));
            propFormatMap.put(BlockProp.RAIL_SHAPE, RailShapePropFormat.INSTANCE);
            propFormatMap.put(BlockProp.ROTATABLE_ROTATION, new BlockFacePropFormat(RotatableRotationProp::new));
            propFormatMap.put(BlockProp.SNOWABLE_SNOWY, new BooleanPropFormat(SnowableSnowyProp::new));
            propFormatMap.put(BlockProp.WATERLOGGED, new BooleanPropFormat(WaterloggedProp::new));
        }

        FORMATS = Map.copyOf(propFormatMap);
    }

    public static Map<String, BlockPropFormat<?>> getFormats() {
        return FORMATS;
    }

}
