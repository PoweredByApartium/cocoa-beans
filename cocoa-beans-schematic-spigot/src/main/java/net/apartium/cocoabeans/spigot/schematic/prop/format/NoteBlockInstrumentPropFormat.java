package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.NoteBlockInstrumentProp;
import org.bukkit.Instrument;

import java.util.function.Function;

public class NoteBlockInstrumentPropFormat extends EnumPropFormat<Instrument> {

    public static final NoteBlockInstrumentPropFormat INSTANCE = new NoteBlockInstrumentPropFormat(NoteBlockInstrumentProp::new);

    public NoteBlockInstrumentPropFormat(Function<Instrument, BlockProp<Instrument>> constructor) {
        super(
                Instrument.class,
                Instrument::values,
                constructor
        );
    }

}
