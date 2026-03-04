package net.apartium.cocoabeans.schematic.format;

import org.jetbrains.annotations.ApiStatus;

import java.io.*;

import static net.apartium.cocoabeans.utils.BufferUtils.*;

/**
 * Immutable metadata record that describes a single compressed body section within a CBSC
 * schematic file (block-data section, index section, or a body extension).
 *
 * <h2>Binary layout ({@value #SIZE} bytes)</h2>
 * <pre>
 *   [1 byte]  compressionType   – identifier of the {@link net.apartium.cocoabeans.schematic.compression.CompressionEngine} used
 *   [8 bytes] uncompressedSize  – original byte count before compression (unsigned 64-bit)
 *   [8 bytes] compressedSize    – byte count as stored in the file (unsigned 64-bit)
 *   [8 bytes] offset            – absolute byte offset of the compressed data in the file (unsigned 64-bit)
 *   [8 bytes] checksum          – CRC-32 of the uncompressed data, stored as unsigned 64-bit
 * </pre>
 *
 * <p>An instance of this record is stored as a placeholder in the header section while the file
 * is being written, and is back-patched with the real values once the body section has been
 * compressed and its final position is known.</p>
 */
@ApiStatus.AvailableSince("0.0.46")
public record CompressionBlockInfo(
        /** Identifies the compression algorithm applied to the body section. */
        byte compressionType,
        /** Byte count of the body section before compression. */
        long uncompressedSize,
        /** Byte count of the body section as stored in the file (after compression). */
        long compressedSize,
        /** Absolute byte offset within the file at which the compressed body section begins. */
        long offset,
        /** CRC-32 checksum of the uncompressed body section, used for integrity verification. */
        long checksum
) {

    /** Fixed serialised size of this record in bytes ({@value}). */
    public static final int SIZE = 33;

    /**
     * Parse CompressionBlockInfo from bytes
     * @param data should be 33 bytes
     * @return new instance of compression block info
     */
    static CompressionBlockInfo fromBytes(byte[] data) throws IOException {
        if (data.length != SIZE)
            throw new EOFException("Bad data length not matching expected size\nExpected: " + SIZE + "\nGot: " + data.length);

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(in);

        return new CompressionBlockInfo(
                (byte) in.read(),
                readU64(din),
                readU64(din),
                readU64(din),
                readU64(din)
        );
    }

    byte[] toByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(33);

        out.write(compressionType);
        out.write(writeU64(uncompressedSize));
        out.write(writeU64(compressedSize));
        out.write(writeU64(offset));
        out.write(writeU64(checksum));

        return out.toByteArray();
    }

}
