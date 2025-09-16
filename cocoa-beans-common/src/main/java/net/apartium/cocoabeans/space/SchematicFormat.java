package net.apartium.cocoabeans.space;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface SchematicFormat {

    static SchematicFormat JSON = new JsonSchematicFormat();

    static SchematicFormat byName(String name) {
        if ("json".equals(name))
            return JSON;
        else
            throw new UnsupportedOperationException("Unsupported format: " + name);
    }

    void write(Schematic schematic, OutputStream stream);

    Schematic read(InputStream stream);

}
