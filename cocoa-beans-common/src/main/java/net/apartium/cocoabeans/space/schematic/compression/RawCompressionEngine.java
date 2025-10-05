package net.apartium.cocoabeans.space.schematic.compression;

public class RawCompressionEngine implements CompressionEngine {

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
