package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.BisectedHalfProp;
import org.bukkit.block.data.Bisected;

import java.util.function.Function;

public class BisectedHalfPropFormat extends EnumPropFormat<Bisected.Half> {

    public static final BisectedHalfPropFormat INSTANCE = new BisectedHalfPropFormat();

    private BisectedHalfPropFormat() {
        this(BisectedHalfProp::new);
    }

    public BisectedHalfPropFormat(Function<Bisected.Half, BlockProp<Bisected.Half>> constructor) {
        super(
                Bisected.Half.class,
                Bisected.Half::values,
                constructor
        );
    }

}
