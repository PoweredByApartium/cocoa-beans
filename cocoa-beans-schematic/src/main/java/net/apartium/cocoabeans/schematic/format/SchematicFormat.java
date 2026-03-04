package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface SchematicFormat<T extends Schematic> {

    void write(T schematic, SeekableOutputStream out);

    T read(SeekableInputStream in);

}
