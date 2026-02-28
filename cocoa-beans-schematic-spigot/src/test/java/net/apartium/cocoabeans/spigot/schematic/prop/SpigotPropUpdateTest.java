package net.apartium.cocoabeans.spigot.schematic.prop;

import org.bukkit.Axis;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

class SpigotPropUpdateTest {

    // region AgeableAgeProp

    @Test
    void ageableAgeProp_updatesAge() {
        Ageable ageable = mock(Ageable.class);
        new AgeableAgeProp(5).update(ageable);
        verify(ageable).setAge(5);
    }

    @Test
    void ageableAgeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new AgeableAgeProp(5).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region AnaloguePowerablePowerProp

    @Test
    void analoguePowerablePowerProp_updatesPower() {
        AnaloguePowerable powerable = mock(AnaloguePowerable.class);
        new AnaloguePowerablePowerProp(10).update(powerable);
        verify(powerable).setPower(10);
    }

    @Test
    void analoguePowerablePowerProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new AnaloguePowerablePowerProp(10).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region AttachableAttachedProp

    @Test
    void attachableAttachedProp_setsAttachedTrue() {
        Attachable attachable = mock(Attachable.class);
        new AttachableAttachedProp(true).update(attachable);
        verify(attachable).setAttached(true);
    }

    @Test
    void attachableAttachedProp_setsAttachedFalse() {
        Attachable attachable = mock(Attachable.class);
        new AttachableAttachedProp(false).update(attachable);
        verify(attachable).setAttached(false);
    }

    @Test
    void attachableAttachedProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new AttachableAttachedProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BambooProp

    @Test
    void bambooProp_setsLeaves() {
        Bamboo bamboo = mock(Bamboo.class);
        new BambooProp(Bamboo.Leaves.LARGE).update(bamboo);
        verify(bamboo).setLeaves(Bamboo.Leaves.LARGE);
    }

    @Test
    void bambooProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BambooProp(Bamboo.Leaves.LARGE).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BeeHiveHoneyLevelProp

    @Test
    void beeHiveHoneyLevelProp_setsHoneyLevel() {
        Beehive beehive = mock(Beehive.class);
        new BeeHiveHoneyLevelProp(3).update(beehive);
        verify(beehive).setHoneyLevel(3);
    }

    @Test
    void beeHiveHoneyLevelProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BeeHiveHoneyLevelProp(3).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BigDripleafTiltProp

    @Test
    void bigDripleafTiltProp_setsTilt() {
        BigDripleaf bigDripleaf = mock(BigDripleaf.class);
        new BigDripleafTiltProp(BigDripleaf.Tilt.FULL).update(bigDripleaf);
        verify(bigDripleaf).setTilt(BigDripleaf.Tilt.FULL);
    }

    @Test
    void bigDripleafTiltProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BigDripleafTiltProp(BigDripleaf.Tilt.FULL).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BisectedHalfProp

    @Test
    void bisectedHalfProp_setsHalf() {
        Bisected bisected = mock(Bisected.class);
        new BisectedHalfProp(Bisected.Half.TOP).update(bisected);
        verify(bisected).setHalf(Bisected.Half.TOP);
    }

    @Test
    void bisectedHalfProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BisectedHalfProp(Bisected.Half.TOP).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BrewingStandBottlesProp

    @Test
    void brewingStandBottlesProp_setsBottles() {
        BrewingStand brewingStand = mock(BrewingStand.class);
        new BrewingStandBottlesProp(new int[]{0, 2}).update(brewingStand);
        verify(brewingStand).setBottle(0, true);
        verify(brewingStand).setBottle(2, true);
    }

    @Test
    void brewingStandBottlesProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BrewingStandBottlesProp(new int[]{0}).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BubbleColumnProp

    @Test
    void bubbleColumnProp_setsDragTrue() {
        BubbleColumn bubbleColumn = mock(BubbleColumn.class);
        new BubbleColumnProp(true).update(bubbleColumn);
        verify(bubbleColumn).setDrag(true);
    }

    @Test
    void bubbleColumnProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BubbleColumnProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region CakeBitesProp

    @Test
    void cakeBitesProp_setsBites() {
        Cake cake = mock(Cake.class);
        new CakeBitesProp(4).update(cake);
        verify(cake).setBites(4);
    }

    @Test
    void cakeBitesProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new CakeBitesProp(4).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region CampfireSignalFireProp

    @Test
    void campfireSignalFireProp_setsSignalFire() {
        Campfire campfire = mock(Campfire.class);
        new CampfireSignalFireProp(true).update(campfire);
        verify(campfire).setSignalFire(true);
    }

    @Test
    void campfireSignalFireProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new CampfireSignalFireProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region CandleProp

    @Test
    void candleProp_setsCandles() {
        Candle candle = mock(Candle.class);
        new CandleProp(3).update(candle);
        verify(candle).setCandles(3);
    }

    @Test
    void candleProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new CandleProp(3).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region CaveVinesPlantBerriesProp

    @Test
    void caveVinesPlantBerriesProp_setsBerries() {
        CaveVinesPlant caveVinesPlant = mock(CaveVinesPlant.class);
        new CaveVinesPlantBerriesProp(true).update(caveVinesPlant);
        verify(caveVinesPlant).setBerries(true);
    }

    @Test
    void caveVinesPlantBerriesProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new CaveVinesPlantBerriesProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region ChestTypeProp

    @Test
    void chestTypeProp_setsType() {
        Chest chest = mock(Chest.class);
        new ChestTypeProp(Chest.Type.LEFT).update(chest);
        verify(chest).setType(Chest.Type.LEFT);
    }

    @Test
    void chestTypeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new ChestTypeProp(Chest.Type.LEFT).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region CommandBlockConditionalProp

    @Test
    void commandBlockConditionalProp_setsConditional() {
        CommandBlock commandBlock = mock(CommandBlock.class);
        new CommandBlockConditionalProp(true).update(commandBlock);
        verify(commandBlock).setConditional(true);
    }

    @Test
    void commandBlockConditionalProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new CommandBlockConditionalProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region ComparatorModeProp

    @Test
    void comparatorModeProp_setsMode() {
        Comparator comparator = mock(Comparator.class);
        new ComparatorModeProp(Comparator.Mode.SUBTRACT).update(comparator);
        verify(comparator).setMode(Comparator.Mode.SUBTRACT);
    }

    @Test
    void comparatorModeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new ComparatorModeProp(Comparator.Mode.SUBTRACT).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region DayLightDetectorInvertedProp

    @Test
    void dayLightDetectorInvertedProp_setsInverted() {
        DaylightDetector detector = mock(DaylightDetector.class);
        new DayLightDetectorInvertedProp(true).update(detector);
        verify(detector).setInverted(true);
    }

    @Test
    void dayLightDetectorInvertedProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new DayLightDetectorInvertedProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region DirectionalFaceProp

    @Test
    void directionalFaceProp_setsFacing() {
        Directional directional = mock(Directional.class);
        new DirectionalFaceProp(BlockFace.NORTH).update(directional);
        verify(directional).setFacing(BlockFace.NORTH);
    }

    @Test
    void directionalFaceProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new DirectionalFaceProp(BlockFace.NORTH).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region DispenserTriggeredProp

    @Test
    void dispenserTriggeredProp_setsTriggered() {
        Dispenser dispenser = mock(Dispenser.class);
        new DispenserTriggeredProp(true).update(dispenser);
        verify(dispenser).setTriggered(true);
    }

    @Test
    void dispenserTriggeredProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new DispenserTriggeredProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region DoorHingeProp

    @Test
    void doorHingeProp_setsHinge() {
        Door door = mock(Door.class);
        new DoorHingeProp(Door.Hinge.RIGHT).update(door);
        verify(door).setHinge(Door.Hinge.RIGHT);
    }

    @Test
    void doorHingeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new DoorHingeProp(Door.Hinge.RIGHT).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region EndPortalFrameEyeProp

    @Test
    void endPortalFrameEyeProp_setsEye() {
        EndPortalFrame endPortalFrame = mock(EndPortalFrame.class);
        new EndPortalFrameEyeProp(true).update(endPortalFrame);
        verify(endPortalFrame).setEye(true);
    }

    @Test
    void endPortalFrameEyeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new EndPortalFrameEyeProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region FaceAttachableAttachedFaceProp

    @Test
    void faceAttachableAttachedFaceProp_setsAttachedFace() {
        FaceAttachable faceAttachable = mock(FaceAttachable.class);
        new FaceAttachableAttachedFaceProp(FaceAttachable.AttachedFace.CEILING).update(faceAttachable);
        verify(faceAttachable).setAttachedFace(FaceAttachable.AttachedFace.CEILING);
    }

    @Test
    void faceAttachableAttachedFaceProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new FaceAttachableAttachedFaceProp(FaceAttachable.AttachedFace.CEILING).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region FarmLandMoistureProp

    @Test
    void farmLandMoistureProp_setsMoisture() {
        Farmland farmland = mock(Farmland.class);
        new FarmLandMoistureProp(7).update(farmland);
        verify(farmland).setMoisture(7);
    }

    @Test
    void farmLandMoistureProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new FarmLandMoistureProp(7).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region GateInWallProp

    @Test
    void gateInWallProp_setsInWall() {
        Gate gate = mock(Gate.class);
        new GateInWallProp(true).update(gate);
        verify(gate).setInWall(true);
    }

    @Test
    void gateInWallProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new GateInWallProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region HangableHangingProp

    @Test
    void hangableHangingProp_setsHanging() {
        Hangable hangable = mock(Hangable.class);
        new HangableHangingProp(true).update(hangable);
        verify(hangable).setHanging(true);
    }

    @Test
    void hangableHangingProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new HangableHangingProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region HopperEnabledProp

    @Test
    void hopperEnabledProp_setsEnabled() {
        Hopper hopper = mock(Hopper.class);
        new HopperEnabledProp(true).update(hopper);
        verify(hopper).setEnabled(true);
    }

    @Test
    void hopperEnabledProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new HopperEnabledProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region JigsawOrientationProp

    @Test
    void jigsawOrientationProp_setsOrientation() {
        Jigsaw jigsaw = mock(Jigsaw.class);
        new JigsawOrientationProp(Jigsaw.Orientation.DOWN_EAST).update(jigsaw);
        verify(jigsaw).setOrientation(Jigsaw.Orientation.DOWN_EAST);
    }

    @Test
    void jigsawOrientationProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new JigsawOrientationProp(Jigsaw.Orientation.DOWN_EAST).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region LeavesDistanceProp

    @Test
    void leavesDistanceProp_setsDistance() {
        Leaves leaves = mock(Leaves.class);
        new LeavesDistanceProp(3).update(leaves);
        verify(leaves).setDistance(3);
    }

    @Test
    void leavesDistanceProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new LeavesDistanceProp(3).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region LeavesPersistentProp

    @Test
    void leavesPersistentProp_setsPersistent() {
        Leaves leaves = mock(Leaves.class);
        new LeavesPersistentProp(true).update(leaves);
        verify(leaves).setPersistent(true);
    }

    @Test
    void leavesPersistentProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new LeavesPersistentProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region LevelledLevelProp

    @Test
    void levelledLevelProp_setsLevel() {
        Levelled levelled = mock(Levelled.class);
        new LevelledLevelProp(8).update(levelled);
        verify(levelled).setLevel(8);
    }

    @Test
    void levelledLevelProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new LevelledLevelProp(8).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region LightableLitProp

    @Test
    void lightableLitProp_setsLit() {
        Lightable lightable = mock(Lightable.class);
        new LightableLitProp(true).update(lightable);
        verify(lightable).setLit(true);
    }

    @Test
    void lightableLitProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new LightableLitProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region MultipleFacingFacesProp

    @Test
    void multipleFacingFacesProp_setsFaces() {
        MultipleFacing multipleFacing = mock(MultipleFacing.class);
        new MultipleFacingFacesProp(Map.of(BlockFace.NORTH, true, BlockFace.SOUTH, false))
                .update(multipleFacing);
        verify(multipleFacing).setFace(BlockFace.NORTH, true);
        verify(multipleFacing).setFace(BlockFace.SOUTH, false);
    }

    @Test
    void multipleFacingFacesProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new MultipleFacingFacesProp(Map.of(BlockFace.NORTH, true)).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region NoteBlockInstrumentProp

    @Test
    void noteBlockInstrumentProp_setsInstrument() {
        NoteBlock noteBlock = mock(NoteBlock.class);
        new NoteBlockInstrumentProp(Instrument.BASS_DRUM).update(noteBlock);
        verify(noteBlock).setInstrument(Instrument.BASS_DRUM);
    }

    @Test
    void noteBlockInstrumentProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new NoteBlockInstrumentProp(Instrument.BASS_DRUM).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region NoteBlockNoteProp

    @Test
    void noteBlockNoteProp_setsNote() {
        NoteBlock noteBlock = mock(NoteBlock.class);
        Note note = new Note(0);
        new NoteBlockNoteProp(note).update(noteBlock);
        verify(noteBlock).setNote(note);
    }

    @Test
    void noteBlockNoteProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new NoteBlockNoteProp(new Note(0)).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region OpenableOpenProp

    @Test
    void openableOpenProp_setsOpen() {
        Openable openable = mock(Openable.class);
        new OpenableOpenProp(true).update(openable);
        verify(openable).setOpen(true);
    }

    @Test
    void openableOpenProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new OpenableOpenProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region OrientableAxisProp

    @Test
    void orientableAxisProp_setsAxis() {
        Orientable orientable = mock(Orientable.class);
        new OrientableAxisProp(Axis.Y).update(orientable);
        verify(orientable).setAxis(Axis.Y);
    }

    @Test
    void orientableAxisProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new OrientableAxisProp(Axis.Y).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region PistonExtendedProp

    @Test
    void pistonExtendedProp_setsExtended() {
        Piston piston = mock(Piston.class);
        new PistonExtendedProp(true).update(piston);
        verify(piston).setExtended(true);
    }

    @Test
    void pistonExtendedProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new PistonExtendedProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region PistonHeadIsShortProp

    @Test
    void pistonHeadIsShortProp_setsShort() {
        PistonHead pistonHead = mock(PistonHead.class);
        new PistonHeadIsShortProp(true).update(pistonHead);
        verify(pistonHead).setShort(true);
    }

    @Test
    void pistonHeadIsShortProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new PistonHeadIsShortProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region PointedDripstoneThicknessProp

    @Test
    void pointedDripstoneThicknessProp_setsThickness() {
        PointedDripstone pointedDripstone = mock(PointedDripstone.class);
        new PointedDripstoneThicknessProp(PointedDripstone.Thickness.TIP).update(pointedDripstone);
        verify(pointedDripstone).setThickness(PointedDripstone.Thickness.TIP);
    }

    @Test
    void pointedDripstoneThicknessProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new PointedDripstoneThicknessProp(PointedDripstone.Thickness.TIP).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region PointedDripstoneVerticalDirectionProp

    @Test
    void pointedDripstoneVerticalDirectionProp_setsVerticalDirection() {
        PointedDripstone pointedDripstone = mock(PointedDripstone.class);
        new PointedDripstoneVerticalDirectionProp(BlockFace.UP).update(pointedDripstone);
        verify(pointedDripstone).setVerticalDirection(BlockFace.UP);
    }

    @Test
    void pointedDripstoneVerticalDirectionProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new PointedDripstoneVerticalDirectionProp(BlockFace.UP).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region PowerablePoweredProp

    @Test
    void powerablePoweredProp_setsPowered() {
        Powerable powerable = mock(Powerable.class);
        new PowerablePoweredProp(true).update(powerable);
        verify(powerable).setPowered(true);
    }

    @Test
    void powerablePoweredProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new PowerablePoweredProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region RailShapeProp

    @Test
    void railShapeProp_setsShape() {
        Rail rail = mock(Rail.class);
        new RailShapeProp(Rail.Shape.ASCENDING_EAST).update(rail);
        verify(rail).setShape(Rail.Shape.ASCENDING_EAST);
    }

    @Test
    void railShapeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new RailShapeProp(Rail.Shape.ASCENDING_EAST).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region RedstoneWireConnectionsProp

    @Test
    void redstoneWireConnectionsProp_setsConnections() {
        RedstoneWire wire = mock(RedstoneWire.class);
        new RedstoneWireConnectionsProp(Map.of(
                BlockFace.EAST, RedstoneWire.Connection.SIDE,
                BlockFace.WEST, RedstoneWire.Connection.UP
        )).update(wire);
        verify(wire).setFace(BlockFace.EAST, RedstoneWire.Connection.SIDE);
        verify(wire).setFace(BlockFace.WEST, RedstoneWire.Connection.UP);
    }

    @Test
    void redstoneWireConnectionsProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new RedstoneWireConnectionsProp(Map.of(BlockFace.EAST, RedstoneWire.Connection.SIDE)).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region RepeaterDelayProp

    @Test
    void repeaterDelayProp_setsDelay() {
        Repeater repeater = mock(Repeater.class);
        new RepeaterDelayProp(2).update(repeater);
        verify(repeater).setDelay(2);
    }

    @Test
    void repeaterDelayProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new RepeaterDelayProp(2).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region RepeaterLockedProp

    @Test
    void repeaterLockedProp_setsLocked() {
        Repeater repeater = mock(Repeater.class);
        new RepeaterLockedProp(true).update(repeater);
        verify(repeater).setLocked(true);
    }

    @Test
    void repeaterLockedProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new RepeaterLockedProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region RespawnAnchorChargesProp

    @Test
    void respawnAnchorChargesProp_setsCharges() {
        RespawnAnchor respawnAnchor = mock(RespawnAnchor.class);
        new RespawnAnchorChargesProp(4).update(respawnAnchor);
        verify(respawnAnchor).setCharges(4);
    }

    @Test
    void respawnAnchorChargesProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new RespawnAnchorChargesProp(4).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region RotatableRotationProp

    @Test
    void rotatableRotationProp_setsRotation() {
        Rotatable rotatable = mock(Rotatable.class);
        new RotatableRotationProp(BlockFace.SOUTH).update(rotatable);
        verify(rotatable).setRotation(BlockFace.SOUTH);
    }

    @Test
    void rotatableRotationProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new RotatableRotationProp(BlockFace.SOUTH).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SaplingStageProp

    @Test
    void saplingStageProp_setsStage() {
        Sapling sapling = mock(Sapling.class);
        new SaplingStageProp(1).update(sapling);
        verify(sapling).setStage(1);
    }

    @Test
    void saplingStageProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SaplingStageProp(1).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region ScaffoldingBottomProp

    @Test
    void scaffoldingBottomProp_setsBottom() {
        Scaffolding scaffolding = mock(Scaffolding.class);
        new ScaffoldingBottomProp(true).update(scaffolding);
        verify(scaffolding).setBottom(true);
    }

    @Test
    void scaffoldingBottomProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new ScaffoldingBottomProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region ScaffoldingDistanceProp

    @Test
    void scaffoldingDistanceProp_setsDistance() {
        Scaffolding scaffolding = mock(Scaffolding.class);
        new ScaffoldingDistanceProp(5).update(scaffolding);
        verify(scaffolding).setDistance(5);
    }

    @Test
    void scaffoldingDistanceProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new ScaffoldingDistanceProp(5).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SculkCatalystBloomProp

    @Test
    void sculkCatalystBloomProp_setsBloom() {
        SculkCatalyst sculkCatalyst = mock(SculkCatalyst.class);
        new SculkCatalystBloomProp(true).update(sculkCatalyst);
        verify(sculkCatalyst).setBloom(true);
    }

    @Test
    void sculkCatalystBloomProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SculkCatalystBloomProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SculkSensorPhaseProp

    @Test
    void sculkSensorPhaseProp_setsPhase() {
        SculkSensor sculkSensor = mock(SculkSensor.class);
        new SculkSensorPhaseProp(SculkSensor.Phase.ACTIVE).update(sculkSensor);
        verify(sculkSensor).setPhase(SculkSensor.Phase.ACTIVE);
    }

    @Test
    void sculkSensorPhaseProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SculkSensorPhaseProp(SculkSensor.Phase.ACTIVE).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SculkShriekerCanSummonProp

    @Test
    void sculkShriekerCanSummonProp_setsCanSummon() {
        SculkShrieker sculkShrieker = mock(SculkShrieker.class);
        new SculkShriekerCanSummonProp(true).update(sculkShrieker);
        verify(sculkShrieker).setCanSummon(true);
    }

    @Test
    void sculkShriekerCanSummonProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SculkShriekerCanSummonProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SculkShriekerShrinkingProp

    @Test
    void sculkShriekerShrinkingProp_setShrieking() {
        SculkShrieker sculkShrieker = mock(SculkShrieker.class);
        new SculkShriekerShrinkingProp(true).update(sculkShrieker);
        verify(sculkShrieker).setShrieking(true);
    }

    @Test
    void sculkShriekerShrinkingProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SculkShriekerShrinkingProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SeaPicklePicklesProp

    @Test
    void seaPicklePicklesProp_setsPickles() {
        SeaPickle seaPickle = mock(SeaPickle.class);
        new SeaPicklePicklesProp(3).update(seaPickle);
        verify(seaPickle).setPickles(3);
    }

    @Test
    void seaPicklePicklesProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SeaPicklePicklesProp(3).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SlabTypeProp

    @Test
    void slabTypeProp_setsType() {
        Slab slab = mock(Slab.class);
        new SlabTypeProp(Slab.Type.DOUBLE).update(slab);
        verify(slab).setType(Slab.Type.DOUBLE);
    }

    @Test
    void slabTypeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SlabTypeProp(Slab.Type.DOUBLE).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SnowableSnowyProp

    @Test
    void snowableSnowyProp_setsSnowy() {
        Snowable snowable = mock(Snowable.class);
        new SnowableSnowyProp(true).update(snowable);
        verify(snowable).setSnowy(true);
    }

    @Test
    void snowableSnowyProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SnowableSnowyProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region SnowLayersProp

    @Test
    void snowLayersProp_setsLayers() {
        Snow snow = mock(Snow.class);
        new SnowLayersProp(4).update(snow);
        verify(snow).setLayers(4);
    }

    @Test
    void snowLayersProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new SnowLayersProp(4).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region StairsProp

    @Test
    void stairsProp_setsShape() {
        Stairs stairs = mock(Stairs.class);
        new StairsProp(Stairs.Shape.OUTER_LEFT).update(stairs);
        verify(stairs).setShape(Stairs.Shape.OUTER_LEFT);
    }

    @Test
    void stairsProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new StairsProp(Stairs.Shape.OUTER_LEFT).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region StructureBlockModeProp

    @Test
    void structureBlockModeProp_setsMode() {
        StructureBlock structureBlock = mock(StructureBlock.class);
        new StructureBlockModeProp(StructureBlock.Mode.SAVE).update(structureBlock);
        verify(structureBlock).setMode(StructureBlock.Mode.SAVE);
    }

    @Test
    void structureBlockModeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new StructureBlockModeProp(StructureBlock.Mode.SAVE).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region TechnicalPistonTypeProp

    @Test
    void technicalPistonTypeProp_setsType() {
        TechnicalPiston technicalPiston = mock(TechnicalPiston.class);
        new TechnicalPistonTypeProp(TechnicalPiston.Type.STICKY).update(technicalPiston);
        verify(technicalPiston).setType(TechnicalPiston.Type.STICKY);
    }

    @Test
    void technicalPistonTypeProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new TechnicalPistonTypeProp(TechnicalPiston.Type.STICKY).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region TNTUnstableProp

    @Test
    void tntUnstableProp_setsUnstable() {
        TNT tnt = mock(TNT.class);
        new TNTUnstableProp(true).update(tnt);
        verify(tnt).setUnstable(true);
    }

    @Test
    void tntUnstableProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new TNTUnstableProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region TripwireDisarmedProp

    @Test
    void tripwireDisarmedProp_setsDisarmed() {
        Tripwire tripwire = mock(Tripwire.class);
        new TripwireDisarmedProp(true).update(tripwire);
        verify(tripwire).setDisarmed(true);
    }

    @Test
    void tripwireDisarmedProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new TripwireDisarmedProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region TurtleEggEggsProp

    @Test
    void turtleEggEggsProp_setsEggs() {
        TurtleEgg turtleEgg = mock(TurtleEgg.class);
        new TurtleEggEggsProp(3).update(turtleEgg);
        verify(turtleEgg).setEggs(3);
    }

    @Test
    void turtleEggEggsProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new TurtleEggEggsProp(3).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region TurtleEggHatchProp

    @Test
    void turtleEggHatchProp_setsHatch() {
        TurtleEgg turtleEgg = mock(TurtleEgg.class);
        new TurtleEggHatchProp(2).update(turtleEgg);
        verify(turtleEgg).setHatch(2);
    }

    @Test
    void turtleEggHatchProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new TurtleEggHatchProp(2).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region WallHeightsProp

    @Test
    void wallHeightsProp_setsHeights() {
        Wall wall = mock(Wall.class);
        new WallHeightsProp(Map.of(
                BlockFace.NORTH, Wall.Height.TALL,
                BlockFace.EAST, Wall.Height.LOW
        )).update(wall);
        verify(wall).setHeight(BlockFace.NORTH, Wall.Height.TALL);
        verify(wall).setHeight(BlockFace.EAST, Wall.Height.LOW);
    }

    @Test
    void wallHeightsProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new WallHeightsProp(Map.of(BlockFace.NORTH, Wall.Height.TALL)).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region WallUpProp

    @Test
    void wallUpProp_setsUp() {
        Wall wall = mock(Wall.class);
        new WallUpProp(true).update(wall);
        verify(wall).setUp(true);
    }

    @Test
    void wallUpProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new WallUpProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region WaterloggedProp

    @Test
    void waterloggedProp_setsWaterlogged() {
        Waterlogged waterlogged = mock(Waterlogged.class);
        new WaterloggedProp(true).update(waterlogged);
        verify(waterlogged).setWaterlogged(true);
    }

    @Test
    void waterloggedProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new WaterloggedProp(true).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BedPartProp

    @Test
    void bedPartProp_setPart() {
        Bed bed = mock(Bed.class);
        new BedPartProp(Bed.Part.HEAD).update(bed);
        verify(bed).setPart(Bed.Part.HEAD);
    }

    @Test
    void bedPartProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BedPartProp(Bed.Part.HEAD).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

    // region BellAttachmentProp

    @Test
    void bellAttachmentProp_setsAttachment() {
        Bell bell = mock(Bell.class);
        new BellAttachmentProp(Bell.Attachment.CEILING).update(bell);
        verify(bell).setAttachment(Bell.Attachment.CEILING);
    }

    @Test
    void bellAttachmentProp_ignoresWrongType() {
        BlockData blockData = mock(BlockData.class);
        new BellAttachmentProp(Bell.Attachment.CEILING).update(blockData);
        verifyNoInteractions(blockData);
    }

    // endregion

}
