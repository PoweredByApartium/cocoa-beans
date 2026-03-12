package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.FaceAttachableAttachedFaceProp;
import org.bukkit.block.data.FaceAttachable;

import java.util.function.Function;

public class FaceAttachableAttachedFacePropFormat extends EnumPropFormat<FaceAttachable.AttachedFace> {

    public static final FaceAttachableAttachedFacePropFormat INSTANCE = new FaceAttachableAttachedFacePropFormat();

    private FaceAttachableAttachedFacePropFormat() {
        this(FaceAttachableAttachedFaceProp::new);
    }

    public FaceAttachableAttachedFacePropFormat(Function<FaceAttachable.AttachedFace, BlockProp<FaceAttachable.AttachedFace>> constructor) {
        super(
                FaceAttachable.AttachedFace.class,
                FaceAttachable.AttachedFace::values,
                constructor
        );
    }

}
