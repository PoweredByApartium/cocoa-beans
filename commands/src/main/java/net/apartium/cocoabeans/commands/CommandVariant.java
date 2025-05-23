package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.39")
public record CommandVariant(
        CommandInfo info,
        String variant,
        boolean ignoreCase
) {

}
