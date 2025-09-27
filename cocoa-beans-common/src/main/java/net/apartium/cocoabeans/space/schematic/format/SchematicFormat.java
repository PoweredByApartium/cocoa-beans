package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.schematic.Schematic;
import net.apartium.cocoabeans.space.schematic.SimpleBlockDataEncoder;
import net.apartium.cocoabeans.space.schematic.compression.CocoaCompressionEngine;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Map;

public interface SchematicFormat {

    static SchematicFormat JSON = new JsonSchematicFormat();
    static SchematicFormat COCOA = new CocoaSchematicFormat(
            Map.of(
                    SimpleBlockDataEncoder.id, new SimpleBlockDataEncoder(Map.of())
            ),
            new CocoaCompressionEngine()
    );

    static SchematicFormat byName(String name) {
        if ("json".equals(name))
            return JSON;
        else
            throw new UnsupportedOperationException("Unsupported format: " + name);
    }

    void write(Schematic schematic, SeekableOutputStream out);

    Schematic read(SeekableInputStream in);

}
