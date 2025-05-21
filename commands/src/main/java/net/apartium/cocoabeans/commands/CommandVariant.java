package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.RequirementOption;

import java.util.Set;

public record CommandVariant(
        Set<RequirementOption> requirements, // Change to requirement as more simple type
        CommandInfo info,
        SubCommand variant
) {

}
