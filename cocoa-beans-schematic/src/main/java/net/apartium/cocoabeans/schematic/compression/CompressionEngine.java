package net.apartium.cocoabeans.schematic.compression;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface CompressionEngine {

    static CompressionEngine gzip() {
        return GzipCompressionEngine.INSTANCE;
    }

    static CompressionEngine raw() {
        return RawCompressionEngine.INSTANCE;
    }

    byte type();

    byte[] compress(byte[] data);
    byte[] decompress(byte[] data);

}
