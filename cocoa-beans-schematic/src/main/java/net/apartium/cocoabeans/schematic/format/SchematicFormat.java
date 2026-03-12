package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import org.jetbrains.annotations.ApiStatus;

/**
 * Strategy interface for serialising and deserialising {@link Schematic} objects to and from a
 * binary stream.
 *
 * <p>Implementations define a complete binary format, including how block data, spatial indices,
 * metadata, and any optional extensions are encoded. Both streams must be seekable because most
 * formats require back-patching header fields after the body sections have been written.</p>
 *
 * @param <T> the concrete {@link Schematic} type this format produces on read
 * @see CocoaSchematicFormat
 */
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicFormat<T extends Schematic> {

    /**
     * Serialises {@code schematic} and writes the result to {@code out}.
     *
     * @param schematic the schematic to serialise
     * @param out       the seekable output stream to write to
     */
    void write(T schematic, SeekableOutputStream out);

    /**
     * Deserialises a schematic from {@code in} and returns an instance of {@code T}.
     *
     * @param in the seekable input stream positioned at the start of the serialised schematic
     * @return the deserialised schematic
     */
    T read(SeekableInputStream in);

}
