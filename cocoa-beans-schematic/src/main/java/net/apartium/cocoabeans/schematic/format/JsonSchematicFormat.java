package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.Schematic;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

/* package-private */ class JsonSchematicFormat implements SchematicFormat {

    @Override
    public void write(Schematic schematic, SeekableOutputStream stream) {

    }

    @Override
    public Schematic read(SeekableInputStream stream) {
        return null;
    }
}
