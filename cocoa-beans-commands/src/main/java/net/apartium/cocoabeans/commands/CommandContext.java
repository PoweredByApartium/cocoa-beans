/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.option.FlagArgument;
import net.apartium.cocoabeans.commands.option.NamedArgument;
import net.apartium.cocoabeans.commands.option.OptionArgument;
import net.apartium.cocoabeans.commands.option.OptionArgumentValue;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents the context of a command after it has been processed
 */
public interface CommandContext {

    /**
     * @return sender instance
     */
    Sender sender();

    /**
     * @return command info, or empty if error
     * @see CommandContext#error()
     */
    Optional<CommandInfo> commandInfo();

    /**
     * @return error, or empty if no error
     * @see CommandContext#commandInfo()
     */
    Optional<BadCommandResponse> error();

    /**
     * @return a copy of the raw arguments array
     */
    String[] args();

    /**
     * @return label used when executing the command
     */
    String commandName();

    @ApiStatus.AvailableSince("0.0.54")
    default boolean hasError() {
        return error().isPresent();
    }

    // new option / flag system
    @ApiStatus.AvailableSince("0.0.54")
    <T> OptionArgumentValue<T> option(OptionArgument<T> option);

    @ApiStatus.AvailableSince("0.0.54")
    boolean flag(FlagArgument flag);

    @ApiStatus.AvailableSince("0.0.54")
    Set<OptionArgumentValue<?>> options();

    @ApiStatus.AvailableSince("0.0.54")
    Set<FlagArgument> flags();

    default <T> OptionArgumentValue<T> option(String form) {
        return (OptionArgumentValue<T>) options().stream()
                .filter(arg -> arg.argument().form().equalsIgnoreCase(form) || arg.argument().forms().contains(form))
                .findFirst()
                .orElseGet(() -> new OptionArgumentValue<>(
                        null, null, null
                ));
    }

    default boolean flag(String form) {
        return flags().stream()
                .anyMatch(arg -> arg.form().equalsIgnoreCase(form) || arg.forms().contains(form));
    }

    @ApiStatus.AvailableSince("0.0.54")
    default NamedArgument namedArgument(String form) {
        return Stream.concat(
                        options().stream()
                                .map(OptionArgumentValue::argument),
                        flags().stream()
                )
                .filter(arg -> arg.form().equalsIgnoreCase(form) || arg.forms().contains(form))
                .findFirst()
                .orElse(null);
    }

}