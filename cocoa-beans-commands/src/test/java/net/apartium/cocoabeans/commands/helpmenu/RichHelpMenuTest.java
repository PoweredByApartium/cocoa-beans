package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.CommandTestBase;
import net.apartium.cocoabeans.commands.HelpMenu;
import net.apartium.cocoabeans.commands.HelpMenuEntry;
import net.apartium.cocoabeans.commands.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RichHelpMenuTest extends CommandTestBase {

    private RichHelpMenuCommand richHelpMenuCommand;

    @Override
    @BeforeEach
    public void before() {
        super.before();
        richHelpMenuCommand = new RichHelpMenuCommand();
        testCommandManager.addCommand(richHelpMenuCommand);
    }

    private HelpMenu getHelpMenu(Sender sender, String label) {
        richHelpMenuCommand.helpMenu = null;
        testCommandManager.handle(sender, label, new String[]{"help"});
        return richHelpMenuCommand.helpMenu;
    }

    private HelpMenuEntry findEntry(HelpMenu menu, String label) {
        return menu.entries().stream()
                .filter(entry -> entry.labels().contains(label))
                .findFirst()
                .orElse(null);
    }

    @Test
    void label() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertEquals("richhelp", menu.label());
    }

    @Test
    void aliases() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertEquals(2, menu.aliases().size());
        assertTrue(menu.aliases().contains("rh"));
        assertTrue(menu.aliases().contains("rhelp"));
    }

    @Test
    void aliasInvocationProducesHelpMenu() {
        HelpMenu menu = getHelpMenu(sender, "rh");
        assertNotNull(menu);
        assertEquals("rh", menu.label());
        assertTrue(menu.aliases().contains("rhelp"));
        assertFalse(menu.aliases().contains("rh"));
    }

    @Test
    void description() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertEquals("rich help command with many features", menu.info().getDescription().orElse(null));
    }

    @Test
    void longDescription() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertEquals(List.of("line1", "line2"), menu.info().getLongDescription().orElse(null));
    }

    @Test
    void usage() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertEquals("/richhelp <subcommand>", menu.info().getUsage().orElse(null));
    }

    @Test
    void requirementsEmpty() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertTrue(menu.requirements().isEmpty());
    }

    @Test
    void entryCount() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertEquals(6, menu.entries().size());
    }

    @Test
    void optionalArgumentAlsoExposedAsRequiredLabel() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertNotNull(findEntry(menu, "hello <string>"));
        assertNotNull(findEntry(menu, "hello <?string>"));
    }

    @Test
    void hiddenSubCommandExcluded() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        assertTrue(menu.entries().stream().noneMatch(entry -> entry.labels().contains("secret")));
        assertTrue(menu.entries().stream().noneMatch(entry -> entry.labels().contains("help")));
    }

    @Test
    void sectionPopulated() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry infoEntry = findEntry(menu, "info");
        assertNotNull(infoEntry);
        assertEquals("system", infoEntry.section());
    }

    @Test
    void sincePopulated() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry infoEntry = findEntry(menu, "info");
        assertNotNull(infoEntry);
        assertEquals("1.0.0", infoEntry.since());
    }

    @Test
    void idPopulated() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry infoEntry = findEntry(menu, "info");
        assertNotNull(infoEntry);
        assertEquals("rich.info", infoEntry.id());
    }

    @Test
    void defaultMetadataIsNull() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry greetEntry = findEntry(menu, "greet <string>");
        assertNotNull(greetEntry);
        assertNull(greetEntry.section());
        assertNull(greetEntry.since());
        assertNull(greetEntry.id());
    }

    @Test
    void argumentLabelFormat() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry greetEntry = findEntry(menu, "greet <string>");
        assertNotNull(greetEntry);
        assertEquals("greet by name", greetEntry.info().getDescription().orElse(null));
    }

    @Test
    void optionalArgumentLabelFormat() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry helloEntry = findEntry(menu, "hello <?string>");
        assertNotNull(helloEntry);
        assertEquals("optional name", helloEntry.info().getDescription().orElse(null));
    }

    @Test
    void caseSensitiveKeyword() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry caseEntry = findEntry(menu, "CASE");
        assertNotNull(caseEntry);
        assertEquals("case sensitive label", caseEntry.info().getDescription().orElse(null));

        assertNull(findEntry(menu, "case"));
    }

    @Test
    void nestedBranchLabel() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry nestedEntry = findEntry(menu, "nested deep <int>");
        assertNotNull(nestedEntry);
        assertEquals("nested branch", nestedEntry.info().getDescription().orElse(null));
    }

    @Test
    void entryRequirementsEmpty() {
        HelpMenu menu = getHelpMenu(sender, "richhelp");
        HelpMenuEntry infoEntry = findEntry(menu, "info");
        assertNotNull(infoEntry);
        assertTrue(infoEntry.requirements().isEmpty());
    }
}
