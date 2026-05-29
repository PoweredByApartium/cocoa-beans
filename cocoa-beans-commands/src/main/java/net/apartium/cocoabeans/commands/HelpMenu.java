package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;

/**
 * Represents a help menu for commands, containing essential metadata, requirements, and entries.
 * A help menu is associated with specific commands and provides guidance on their usage and descriptions.
 *
 * @param label         The primary label or title of the help menu.
 * @param aliases       A list of alternative aliases for the help menu, allowing it to be accessed
 *                      with different terms.
 * @param info          The command information, including descriptions, usages, and long descriptions
 *                      relevant to the help menu.
 * @param requirements  A set of requirements that must be fulfilled to access or view this help menu.
 *                      These can include permissions or other conditions.
 * @param entries       A list of {@link HelpMenuEntry} instances representing the individual entries
 *                      within the help menu, each containing detailed information about specific
 *                      command sub-sections.
 */
@ApiStatus.AvailableSince("0.0.50")
public record HelpMenu(
        String label,
        List<String> aliases,
        CommandInfo info,
        Set<Requirement> requirements,
        List<HelpMenuEntry> entries
) {

}
