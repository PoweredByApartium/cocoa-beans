package net.apartium.cocoabeans.commands.option;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.54")
public sealed interface FlagArgument extends NamedArgument permits FlagArgumentImpl {
}
