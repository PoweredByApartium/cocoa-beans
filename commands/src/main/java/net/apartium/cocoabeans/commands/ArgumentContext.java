package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

/**
 * Argument context is general context for all arguments
 * Used for all commands & other system like Flag system & compound parser
 * @param commandName command name
 * @param args args
 * @param sender sender
 * @param parsedArgs parsed args
 */
@ApiStatus.AvailableSince("0.0.36")
public record ArgumentContext(
    String commandName,
    String[] args,
    Sender sender,
    Map<Class<?>, List<Object>> parsedArgs
) {}
