package net.apartium.cocoabeans.space.schematic.compression;

public interface CompressionEngine {

    byte type();

    byte[] compress(byte[] data);
    byte[] decompress(byte[] data);

}
