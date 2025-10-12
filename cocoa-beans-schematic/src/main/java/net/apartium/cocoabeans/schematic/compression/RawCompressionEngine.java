package net.apartium.cocoabeans.schematic.compression;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.45")
/* package-private */ class RawCompressionEngine implements CompressionEngine {

    /* package-private */ static final RawCompressionEngine INSTANCE = new RawCompressionEngine();

    @Override
    public byte type() {
        return CompressionType.RAW.getId();
    }

    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }

}
