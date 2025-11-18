package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.spigot.schematic.prop.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.*;

import java.util.HashMap;
import java.util.Map;

public class SpigotSchematicPlacer_1_20_R1 implements SpigotSchematicPlacer {

    @Override
    public void place(Block block, BlockPlacement placement) {
        org.bukkit.block.data.BlockData blockData = getBukkitBlockData(placement.block());

        if (blockData == null) {
            Bukkit.getLogger().warning("Could not convert block data to org.bukkit.block.data.BlockData! (" + placement.block().type().toString() + ")");
            return;
        }

        block.setBlockData(blockData);
    }

    private org.bukkit.block.data.BlockData getBukkitBlockData(BlockData blockData) {
        Material type = Material.matchMaterial(blockData.type().namespace() + ":" + blockData.type().key());
        if (type == null || type == Material.AIR)
            return null;


        Map<String, BlockProp<?>> props = blockData.props();
        org.bukkit.block.data.BlockData block = Bukkit.getServer().createBlockData(type);

        for (BlockProp<?> prop : props.values()) {
            if (!(prop instanceof SpigotPropHandler handler))
                continue;

            handler.update(block);
        }

        return block;

    }

    @Override
    public BlockData getBlockData(Block block) {
        Material type = block.getType();
        Map<String, BlockProp<?>> props = new HashMap<>();

        loadProps(block.getBlockData(), props);

        return new GenericBlockData(
                new NamespacedKey(type.key().namespace(), type.key().value()),
                props
        );
    }

    private void loadProps(org.bukkit.block.data.BlockData blockData, Map<String, BlockProp<?>> props) {
        if (blockData instanceof Stairs stairs)
            props.put(BlockProp.STAIRS_SHAPE, new StairsProp(stairs.getShape()));

        if (blockData instanceof Directional directional)
            props.put(BlockProp.DIRECTIONAL, new DirectionalFaceProp(directional.getFacing()));

        if (blockData instanceof Bamboo bamboo)
            props.put(BlockProp.BAMBOO_LEAVES, new BambooProp(bamboo.getLeaves()));

        if (blockData instanceof Bed bed)
            props.put(BlockProp.BED_PART, new BedPartProp(bed.getPart()));

        if (blockData instanceof Beehive beehive)
            props.put(BlockProp.BEEHIVE_HONEY_LEVEL, new BeeHiveHoneyLevelProp(beehive.getHoneyLevel()));

        if (blockData instanceof Bell bell)
            props.put(BlockProp.BELL_ATTACHMENT, new BellAttachmentProp(bell.getAttachment()));

        if (blockData instanceof BigDripleaf bigDripleaf)
            props.put(BlockProp.BIG_DRIP_LEAF_TILT, new BigDripleafTiltProp(bigDripleaf.getTilt()));

        if (blockData instanceof BrewingStand brewingStand)
            props.put(BlockProp.BREWING_STAND_BOTTLES, new BrewingStandBottlesProp(brewingStand.getBottles().stream().mapToInt(Integer::intValue).toArray()));

        if (blockData instanceof Cake cake)
            props.put(BlockProp.CAKE_BITES, new CakeBitesProp(cake.getBites()));

        if (blockData instanceof Campfire campfire)
            props.put(BlockProp.CAMPFIRE_SIGNAL_FIRE, new CampfireSignalFireProp(campfire.isSignalFire()));

        if (blockData instanceof BubbleColumn bubbleColumn)
            props.put(BlockProp.BUBBLE_COLUMN_DRAG, new BubbleColumnProp(bubbleColumn.isDrag()));

        if (blockData instanceof Candle candle)
            props.put(BlockProp.CANDLE_CANDLES, new CandleProp(candle.getCandles()));

        if (blockData instanceof CaveVinesPlant caveVinesPlant)
            props.put(BlockProp.CAVE_VINES_PLANT_BERRIES, new CaveVinesPlantBerriesProp(caveVinesPlant.isBerries()));

        if (blockData instanceof Chest chest)
            props.put(BlockProp.CHEST_TYPE, new ChestTypeProp(chest.getType()));

        if (blockData instanceof CommandBlock commandBlock)
            props.put(BlockProp.COMMAND_BLOCK_CONDITIONAL, new CommandBlockConditionalProp(commandBlock.isConditional()));

        if (blockData instanceof Comparator comparator)
            props.put(BlockProp.COMPARATOR_MODE, new ComparatorModeProp(comparator.getMode()));

        if (blockData instanceof DaylightDetector daylightDetector)
            props.put(BlockProp.DAY_LIGHT_DETECTOR_INVERTED, new DayLightDetectorInvertedProp(daylightDetector.isInverted()));

        if (blockData instanceof Dispenser dispenser)
            props.put(BlockProp.DISPENSER_TRIGGERED, new DispenserTriggeredProp(dispenser.isTriggered()));

        if (blockData instanceof Door door)
            props.put(BlockProp.DOOR_HINGE, new DoorHingeProp(door.getHinge()));

        if (blockData instanceof EndPortalFrame endPortalFrame)
            props.put(BlockProp.END_PORTAL_FRAME_EYE, new EndPortalFrameEyeProp(endPortalFrame.hasEye()));
    }

}
