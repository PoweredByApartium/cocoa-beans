package net.apartium.cocoabeans.space.schematic.compression;

public interface CompressionEngine {

    byte[] compress(CompressionType type, byte[] data);
    byte[] decompress(CompressionType type, byte[] data);

}
