package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.JigsawOrientationProp;
import org.bukkit.block.data.type.Jigsaw;

import java.util.function.Function;

public class JigsawOrientationPropFormat extends EnumPropFormat<Jigsaw.Orientation> {

    public static final JigsawOrientationPropFormat INSTANCE = new JigsawOrientationPropFormat(JigsawOrientationProp::new);

    public JigsawOrientationPropFormat(Function<Jigsaw.Orientation, BlockProp<Jigsaw.Orientation>> constructor) {
        super(
                Jigsaw.Orientation.class,
                Jigsaw.Orientation::values,
                constructor
        );
    }
}
