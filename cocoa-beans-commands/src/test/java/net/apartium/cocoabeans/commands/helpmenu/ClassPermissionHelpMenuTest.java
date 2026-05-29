package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.CommandTestBase;
import net.apartium.cocoabeans.commands.HelpMenu;
import net.apartium.cocoabeans.commands.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassPermissionHelpMenuTest extends CommandTestBase {

    private ClassPermissionHelpMenuCommand command;

    @Override
    @BeforeEach
    public void before() {
        super.before();
        command = new ClassPermissionHelpMenuCommand();
        testCommandManager.addCommand(command);
    }

    private HelpMenu getHelpMenu(Sender sender, String label) {
        command.helpMenu = null;
        testCommandManager.handle(sender, label, new String[]{"help"});
        return command.helpMenu;
    }

    @Test
    void classLevelRequirementPropagatesToHelpMenu() {
        sender.addPermission("perm.help.class");

        HelpMenu menu = getHelpMenu(sender, "classperm");
        assertNotNull(menu);
        assertFalse(menu.requirements().isEmpty());
    }

    @Test
    void classLevelRequirementPropagatesToEachEntry() {
        sender.addPermission("perm.help.class");

        HelpMenu menu = getHelpMenu(sender, "classperm");
        assertNotNull(menu);

        menu.entries().forEach(entry -> assertFalse(entry.requirements().isEmpty()));
        assertEquals(2, menu.entries().size());
    }

    @Test
    void aliasInvocationKeepsRequirements() {
        sender.addPermission("perm.help.class");

        HelpMenu menu = getHelpMenu(sender, "cp");
        assertNotNull(menu);
        assertEquals("cp", menu.label());
        assertFalse(menu.requirements().isEmpty());
    }

    @Test
    void senderWithoutPermissionCannotInvokeHelp() {
        HelpMenu menu = getHelpMenu(sender, "classperm");
        assertNull(menu);
    }
}
