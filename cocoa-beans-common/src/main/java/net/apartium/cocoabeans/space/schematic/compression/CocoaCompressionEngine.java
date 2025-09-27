package net.apartium.cocoabeans.space.schematic.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CocoaCompressionEngine implements CompressionEngine{

    @Override
    public byte[] compress(CompressionType type, byte[] data) {
        return switch(type) {
            case RAW -> data;
            case GZIP -> gzip(data);
            default -> throw new UnsupportedOperationException("Engine doesn't support compression type of: " + type);
        };
    }



    private byte[] gzip(byte[] data) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();

        try (GZIPOutputStream g = new GZIPOutputStream(b)) {
            g.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return b.toByteArray();
    }

    @Override
    public byte[] decompress(CompressionType type, byte[] data) {
        return switch(type) {
            case RAW -> data;
            case GZIP -> deGzip(data);
            default -> throw new UnsupportedOperationException("Engine doesn't support decompression type of: " + type);
        };
    }

    private byte[] deGzip(byte[] data) {
        try (GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(data))) {
            return gin.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
