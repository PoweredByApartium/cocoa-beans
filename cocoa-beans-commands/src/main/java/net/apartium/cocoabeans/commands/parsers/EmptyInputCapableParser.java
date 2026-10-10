package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

/**
 * Marker interface for argument parsers that support parsing empty input.
 * <p>
 * Implementing this interface allows the command system to invoke
 * the parser even when no input arguments are available.
 * <p>
 * This does not guarantee that parsing will succeed.
 */
@ApiStatus.AvailableSince("0.0.54")
public interface EmptyInputCapableParser {

}
