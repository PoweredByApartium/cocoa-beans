package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.Schematic;

/**
 * Custom data that can be stored on the schematic file.
 * @see Schematic#bodyExtensions()
 * @see BodyExtensionFormat
 */
public interface BodyExtension<T> {

    /**
     * Unique identifier associated with the type of the extension (not its occurrence)
     * @return the unique identifier for this body extension type
     */
    long id();

    /**
     * Body extension compound data
     * @return the data associated with this body extension
     */
    T data();

}
