package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.schematic.*;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;

public interface SchematicFormat {

    void write(Schematic schematic, SeekableOutputStream out);

    Schematic read(SeekableInputStream in);

}
