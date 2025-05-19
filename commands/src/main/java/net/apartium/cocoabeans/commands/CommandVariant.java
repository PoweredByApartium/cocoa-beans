package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.Requirement;

import java.util.Set;

public record CommandVariant(
        Set<Requirement> requirements, // Change to requirement as more simple type
        SubCommand variant
) {

}
