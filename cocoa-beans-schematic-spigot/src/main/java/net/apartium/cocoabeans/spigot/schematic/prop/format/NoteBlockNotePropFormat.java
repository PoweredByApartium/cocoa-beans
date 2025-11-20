package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.NoteBlockNoteProp;
import net.apartium.cocoabeans.utils.FileUtils;
import org.bukkit.Note;

import java.io.*;
import java.util.function.Function;

public class NoteBlockNotePropFormat implements BlockPropFormat<Note> {

    public static final NoteBlockNotePropFormat INSTANCE = new NoteBlockNotePropFormat(NoteBlockNoteProp::new);

    private final Function<Note, BlockProp<Note>> constructor;
    private final boolean onlyNote;

    public NoteBlockNotePropFormat(Function<Note, BlockProp<Note>> constructor) {
        this.constructor = constructor;
        this.onlyNote = Note.class.getConstructors().length == 1
                && Note.class.getConstructors()[0].getParameterCount() == 1;
    }

    @Override
    public BlockProp<Note> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
        try {
            boolean onlyNote = in.readBoolean();

            if (onlyNote) {
                int note = in.readInt();
                return constructor.apply(new Note(note));
            }

            int octave = in.readInt();
            Note.Tone tone = FileUtils.readEnum(in, Note.Tone.class, Note.Tone::values);
            boolean sharped = in.readBoolean();

            return constructor.apply(new Note(octave, tone, sharped));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Note getNoteOrElseThrow(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null)
            throw new IllegalArgumentException("");

        if (!(value instanceof Note note))
            throw new IllegalArgumentException("BlockProp expected Note but got " + value.getClass().getName());

        return note;
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Note note = getNoteOrElseThrow(prop);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);

        try {
            out.writeBoolean(this.onlyNote);

            if (this.onlyNote) {
                out.writeInt(note.getId());
                return byteArray.toByteArray();
            }

            out.writeInt(note.getOctave());
            out.write(FileUtils.writeEnum(note.getTone()));
            out.writeBoolean(note.isSharped());

            return byteArray.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
