package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@ApiStatus.AvailableSince("0.0.50")
public record HelpMenuEntry(
        Set<String> labels,
        CommandInfo info,
        Set<Requirement> requirements,
        String section,
        String since,
        String id
) {
}
