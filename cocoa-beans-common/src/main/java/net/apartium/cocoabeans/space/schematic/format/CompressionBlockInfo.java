package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.schematic.compression.CompressionType;

import java.io.*;

import static net.apartium.cocoabeans.space.schematic.utils.FileUtils.*;

public record CompressionBlockInfo(
        byte compressionType,
        long uncompressedSize,
        long compressedSize,
        long offset,
        long checksum
) {

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
