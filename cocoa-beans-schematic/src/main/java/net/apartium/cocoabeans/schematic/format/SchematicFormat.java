package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.45")
public interface SchematicFormat {

    void write(Schematic schematic, SeekableOutputStream out);

    Schematic read(SeekableInputStream in);

}
