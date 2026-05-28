package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;

@ApiStatus.AvailableSince("0.0.50")
public record HelpMenu(
        String label,
        List<String> aliases,
        CommandInfo info,
        Set<Requirement> requirements,
        List<HelpMenuEntry> entries
) {

}
