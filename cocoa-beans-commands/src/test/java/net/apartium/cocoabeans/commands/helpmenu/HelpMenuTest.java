package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.CommandTestBase;
import net.apartium.cocoabeans.commands.HelpMenu;
import net.apartium.cocoabeans.commands.HelpMenuEntry;
import net.apartium.cocoabeans.commands.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelpMenuTest extends CommandTestBase {

    private HelpMenuCommand helpMenuCommand;

    @Override
    @BeforeEach
    public void before() {
        super.before();
        helpMenuCommand = new HelpMenuCommand();
        testCommandManager.addCommand(helpMenuCommand);
    }

    HelpMenu getHelpMenu(Sender sender) {
        testCommandManager.handle(sender, "help", new String[]{"help"});
        return helpMenuCommand.helpMenu;
    }

    @Test
    void test() {
        HelpMenu helpMenu = getHelpMenu(sender);

        assertEquals("help", helpMenu.label());
        assertTrue(helpMenu.aliases().isEmpty());
        assertEquals("wow this is command with help menu", helpMenu.info().getDescription().orElse(null));
        assertTrue(helpMenu.requirements().isEmpty());
        assertEquals(3, helpMenu.entries().size());

        HelpMenuEntry testEntry = helpMenu.entries().stream().filter(entry -> entry.labels().contains("test")).findFirst().orElse(null);
        assertNotNull(testEntry);

        assertEquals("this command don't take argument after test", testEntry.info().getDescription().orElse(null));
    }
}
