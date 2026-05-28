package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.CommandInfo;
import net.apartium.cocoabeans.commands.HelpMenu;
import net.apartium.cocoabeans.commands.HelpMenuEntry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HelpMenuRecordTest {

    private static CommandInfo info(String description) {
        return new CommandInfo(List.of(description), List.of(), List.of());
    }

    @Test
    void helpMenuAccessors() {
        CommandInfo info = info("desc");
        HelpMenuEntry entry = new HelpMenuEntry(Set.of("a"), info, Set.of(), "section", "1.0", "id");
        HelpMenu menu = new HelpMenu("label", List.of("alias"), info, Set.of(), List.of(entry));

        assertEquals("label", menu.label());
        assertEquals(List.of("alias"), menu.aliases());
        assertEquals(info, menu.info());
        assertTrue(menu.requirements().isEmpty());
        assertEquals(List.of(entry), menu.entries());
    }

    @Test
    void helpMenuEntryAccessors() {
        CommandInfo info = info("entry desc");
        HelpMenuEntry entry = new HelpMenuEntry(
                Set.of("first", "second"),
                info,
                Set.of(),
                "sectionA",
                "2.0",
                "myId"
        );

        assertEquals(Set.of("first", "second"), entry.labels());
        assertEquals(info, entry.info());
        assertTrue(entry.requirements().isEmpty());
        assertEquals("sectionA", entry.section());
        assertEquals("2.0", entry.since());
        assertEquals("myId", entry.id());
    }

    @Test
    void helpMenuEquality() {
        CommandInfo info = info("d");
        HelpMenuEntry entry = new HelpMenuEntry(Set.of("x"), info, Set.of(), null, null, null);

        HelpMenu a = new HelpMenu("label", List.of(), info, Set.of(), List.of(entry));
        HelpMenu b = new HelpMenu("label", List.of(), info, Set.of(), List.of(entry));
        HelpMenu c = new HelpMenu("other", List.of(), info, Set.of(), List.of(entry));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void helpMenuEntryEquality() {
        CommandInfo info = info("d");

        HelpMenuEntry a = new HelpMenuEntry(Set.of("x"), info, Set.of(), null, null, null);
        HelpMenuEntry b = new HelpMenuEntry(Set.of("x"), info, Set.of(), null, null, null);
        HelpMenuEntry c = new HelpMenuEntry(Set.of("y"), info, Set.of(), null, null, null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void helpMenuTolerateNullableMetadata() {
        CommandInfo info = info("d");

        HelpMenuEntry entry = new HelpMenuEntry(Set.of("x"), info, Set.of(), null, null, null);
        assertNull(entry.section());
        assertNull(entry.since());
        assertNull(entry.id());
    }
}
