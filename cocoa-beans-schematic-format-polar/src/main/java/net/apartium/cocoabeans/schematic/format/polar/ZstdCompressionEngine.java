package net.apartium.cocoabeans.schematic.format.polar;

import com.github.luben.zstd.Zstd;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.compression.CompressionType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class ZstdCompressionEngine implements CompressionEngine {

    static final ZstdCompressionEngine INSTANCE = new ZstdCompressionEngine();

    private ZstdCompressionEngine() {}

    @Override
    public byte type() {
        return CompressionType.ZSTD.getId();
    }

    @Override
    public byte[] compress(byte[] data) {
        return Zstd.compress(data);
    }

    @Override
    public byte[] decompress(byte[] data) {
        long decompressedSize = Zstd.decompressedSize(data);
        if (decompressedSize <= 0)
            throw new IllegalArgumentException("Unable to determine decompressed size");

        return Zstd.decompress(data, (int) decompressedSize);
    }

}