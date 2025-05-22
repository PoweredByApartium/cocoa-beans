package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.RequirementOption;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@ApiStatus.AvailableSince("0.0.39")
public record CommandVariant(
        Set<RequirementOption> requirements, // Change to requirement as more simple type
        CommandInfo info,
        String variant,
        boolean ignoreCase
) {

}
