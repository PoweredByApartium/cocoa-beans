package net.apartium.cocoabeans.schematic.format;

import java.io.IOException;
import java.io.InputStream;

/**
 * Encodes and decodes a body extension
 * @see net.apartium.cocoabeans.schematic.Schematic#bodyExtensions()
 * @see BodyExtension
 * @param <T> the type of the body extension data
 */
public interface BodyExtensionFormat<T> {

    /**
     * Reads a body extension from the input stream. The size parameter indicates the number of bytes that should be read for this extension.
     * @param in the input stream to read from
     * @param size the number of bytes to read for this extension
     * @return the body extension read from the input stream
     * @throws IOException if an I/O error occurs while reading the body extension
     */
    BodyExtension<T> read(InputStream in, long size) throws IOException;

    /**
     * Writes a body extension to a byte array. The returned byte array should contain the serialized form of the body extension, which can be written to an output stream.
     * @param extension the body extension to write
     * @return a byte array containing the serialized form of the body extension
     * @throws IOException if an I/O error occurs while writing the body extension
     */
    byte[] write(BodyExtension<T> extension) throws IOException;

}
