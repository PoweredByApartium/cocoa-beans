package net.apartium.cocoabeans.schematic.format;

import java.io.IOException;
import java.io.InputStream;

public interface BodyExtensionFormat<T> {

    BodyExtension<T> read(InputStream in, long size) throws IOException;
    byte[] write(BodyExtension<T> extension) throws IOException;

}
