package net.apartium.cocoabeans.schematic.compression;

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
