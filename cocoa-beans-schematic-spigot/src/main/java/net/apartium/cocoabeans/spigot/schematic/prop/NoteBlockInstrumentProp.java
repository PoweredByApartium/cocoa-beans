package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.Instrument;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;

public record NoteBlockInstrumentProp(Instrument value) implements BlockProp<Instrument>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof NoteBlock noteBlock))
            return;

        noteBlock.setInstrument(value);
    }

}
