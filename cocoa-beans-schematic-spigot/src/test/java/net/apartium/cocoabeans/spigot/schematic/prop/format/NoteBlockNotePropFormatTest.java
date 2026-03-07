package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.spigot.schematic.prop.NoteBlockNoteProp;
import org.bukkit.Note;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class NoteBlockNotePropFormatTest {

    private static boolean onlyNoteConstructor() {
        return Note.class.getConstructors().length == 1
                && Note.class.getConstructors()[0].getParameterCount() == 1;
    }

    @Test
    void roundtripNoteById() {
        NoteBlockNotePropFormat format = NoteBlockNotePropFormat.INSTANCE;

        Note note = new Note(5);
        byte[] encoded = format.encode(new NoteBlockNoteProp(note));

        Note decoded = format.decode(encoded).value();
        assertEquals(note.getId(), decoded.getId());
    }

    @Test
    void roundtripNoteComponentsWhenSupported() {
        if (onlyNoteConstructor()) {
            return;
        }

        NoteBlockNotePropFormat format = NoteBlockNotePropFormat.INSTANCE;

        Note.Tone tone = Note.Tone.values()[0];
        Note note = new Note(1, tone, true);
        byte[] encoded = format.encode(new NoteBlockNoteProp(note));

        Note decoded = format.decode(encoded).value();
        assertEquals(note.getId(), decoded.getId());
        assertEquals(note.getOctave(), decoded.getOctave());
        assertEquals(note.getTone(), decoded.getTone());
        assertEquals(note.isSharped(), decoded.isSharped());
    }

    @Test
    void encodeRejectsNullValue() {
        NoteBlockNotePropFormat format = NoteBlockNotePropFormat.INSTANCE;

        assertThrowsExactly(NullPointerException.class, () -> format.encode(() -> null));
    }

    @Test
    void encodeRejectsWrongValueType() {
        NoteBlockNotePropFormat format = NoteBlockNotePropFormat.INSTANCE;

        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> "nope"));
    }

    @Test
    void decodeRejectsInvalidData() {
        NoteBlockNotePropFormat format = NoteBlockNotePropFormat.INSTANCE;

        assertThrowsExactly(UncheckedIOException.class, () -> format.decode(new byte[0]));
    }
}
