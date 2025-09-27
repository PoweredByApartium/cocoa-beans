package net.apartium.cocoabeans.space.schematic.format;

import net.apartium.cocoabeans.space.schematic.Schematic;
import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.space.schematic.utils.SeekableOutputStream;

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
