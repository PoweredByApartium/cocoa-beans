package net.apartium.cocoabeans.space.schematic.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompressionEngine implements CompressionEngine {

    @Override
    public byte type() {
        return CompressionType.GZIP.getId();
    }

    @Override
    public byte[] compress(byte[] data) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();

        try (GZIPOutputStream g = new GZIPOutputStream(b)) {
            g.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return b.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] data) {
        try (GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(data))) {
            return gin.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
