package net.apartium.cocoabeans.schematic.compression;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a compression engine.
 * This class contains the compression logic itself
 * @see CompressionType
 */
@ApiStatus.AvailableSince("0.0.46")
public interface CompressionEngine {

    /**
     * Returns the gzip compression engine instance
     * @return compression engine instance
     */
    static CompressionEngine gzip() {
        return GzipCompressionEngine.INSTANCE;
    }

    /**
     * Returns the raw compression engine instance
     * @return compression engine instance
     */
    static CompressionEngine raw() {
        return RawCompressionEngine.INSTANCE;
    }

    /**
     * Compression format type
     * @see CompressionType#getId()
     * @return compression type
     */
    byte type();

    /**
     * Compress given data
     * @param data data to compress
     * @return compressed data
     */
    byte[] compress(byte[] data);

    /**
     * Decompress given data
     * @param data compressed data
     * @return decompressed data
     */
    byte[] decompress(byte[] data);

}
