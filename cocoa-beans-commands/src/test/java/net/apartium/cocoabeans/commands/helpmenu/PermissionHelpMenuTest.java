package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.CommandTestBase;
import net.apartium.cocoabeans.commands.HelpMenu;
import net.apartium.cocoabeans.commands.HelpMenuEntry;
import net.apartium.cocoabeans.commands.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PermissionHelpMenuTest extends CommandTestBase {

    private PermissionHelpMenuCommand command;

    @Override
    @BeforeEach
    public void before() {
        super.before();
        command = new PermissionHelpMenuCommand();
        testCommandManager.addCommand(command);
    }

    private HelpMenu getHelpMenu(Sender sender) {
        command.helpMenu = null;
        testCommandManager.handle(sender, "permhelp", new String[]{"help"});
        return command.helpMenu;
    }

    private Set<String> entryLabels(HelpMenu menu) {
        return menu.entries().stream()
                .map(HelpMenuEntry::label)
                .collect(Collectors.toSet());
    }

    @Test
    void senderWithoutPermissionOnlySeesPublic() {
        HelpMenu menu = getHelpMenu(sender);
        assertNotNull(menu);

        Set<String> labels = entryLabels(menu);
        assertTrue(labels.contains("public"));
        assertFalse(labels.contains("admin"));
        assertFalse(labels.contains("staff"));
    }

    @Test
    void senderWithStaffPermissionSeesPublicAndStaff() {
        sender.addPermission("perm.help.staff");

        HelpMenu menu = getHelpMenu(sender);
        Set<String> labels = entryLabels(menu);
        assertTrue(labels.contains("public"));
        assertTrue(labels.contains("staff"));
        assertFalse(labels.contains("admin"));
    }

    @Test
    void senderWithAllPermissionsSeesEverything() {
        sender.addPermission("perm.help.admin");
        sender.addPermission("perm.help.staff");

        HelpMenu menu = getHelpMenu(sender);
        Set<String> labels = entryLabels(menu);
        assertTrue(labels.contains("public"));
        assertTrue(labels.contains("admin"));
        assertTrue(labels.contains("staff"));
        assertEquals(3, menu.entries().size());
    }

    @Test
    void permittedEntryCarriesItsRequirements() {
        sender.addPermission("perm.help.admin");

        HelpMenu menu = getHelpMenu(sender);
        HelpMenuEntry adminEntry = menu.entries().stream()
                .filter(entry -> entry.label().equals("admin"))
                .findFirst()
                .orElse(null);

        assertNotNull(adminEntry);
        assertFalse(adminEntry.requirements().isEmpty());
    }

    @Test
    void publicEntryHasNoRequirements() {
        HelpMenu menu = getHelpMenu(sender);
        HelpMenuEntry publicEntry = menu.entries().stream()
                .filter(entry -> entry.label().equals("public"))
                .findFirst()
                .orElse(null);

        assertNotNull(publicEntry);
        assertTrue(publicEntry.requirements().isEmpty());
    }

    @Test
    void helpMenuLevelRequirementsEmptyWhenNoClassLevelRequirement() {
        HelpMenu menu = getHelpMenu(sender);
        assertTrue(menu.requirements().isEmpty());
    }
}
