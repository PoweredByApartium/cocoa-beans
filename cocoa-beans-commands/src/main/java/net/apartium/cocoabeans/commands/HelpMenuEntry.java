package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * Represents an entry in the help menu, which provides information about commands and their usage.
 *
 * This class holds metadata about a specific command, including its labels, detailed
 * information, requirements for execution, organizational section grouping, and versioning details.
 *
 * @param label         the label of the command
 * @param info          detailed information about the command, including descriptions and usage examples
 * @param requirements  the set of requirements that must be met to execute the command
 * @param section       the section or category to which this command belongs in the help menu
 * @param since         the version since this command has been available
 * @param id            the unique identifier for this help menu entry
 */
@ApiStatus.AvailableSince("0.0.50")
public record HelpMenuEntry(
        String label,
        CommandInfo info,
        Set<Requirement> requirements,
        String section,
        String since,
        String id
) {
}
