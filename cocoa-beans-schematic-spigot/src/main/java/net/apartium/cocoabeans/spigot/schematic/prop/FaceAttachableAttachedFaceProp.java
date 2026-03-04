package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;

public record FaceAttachableAttachedFaceProp(FaceAttachable.AttachedFace attachedFace) implements BlockProp<FaceAttachable.AttachedFace>, SpigotPropHandler {

    @Override
    public FaceAttachable.AttachedFace value() {
        return attachedFace;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof FaceAttachable faceAttachable))
            return;

        faceAttachable.setAttachedFace(attachedFace);
    }
}
