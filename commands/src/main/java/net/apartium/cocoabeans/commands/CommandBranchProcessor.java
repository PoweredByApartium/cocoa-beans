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

import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.requirements.RequirementSet;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* package-private */ class CommandBranchProcessor {

    private final CommandManager commandManager;

    /* package-private */ final List<Entry<RequirementSet, CommandOption>> objectMap = new ArrayList<>();

    CommandBranchProcessor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /* package-private */ @Nullable CommandContext handle(RegisteredCommand commandWrapper, String commandName, String[] args, Sender sender, int index) {

        CommandException commandException = null;

        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            try {
                if (!entry.key().meetsRequirements(sender, commandName, args, index))
                    continue;
            } catch (CommandException e) {
                if (commandException == null || commandException.getDepth() < e.getDepth())
                    commandException = e;

                continue;
            }


            if (args.length <= index) {
                if (!entry.value().getOptionalArgumentTypeHandlerMap().isEmpty()) {
                    CommandContext commandContext = null;
                    try {
                        commandContext = entry.value().handleOptional(commandWrapper, commandName, args, sender, index);
                    } catch (Throwable e) {
                        if (!(e instanceof CommandException))
                            e = new CommandException(commandName, args, index, e.getMessage(), e);

                        if (commandException == null || commandException.getDepth() < ((CommandException) e).getDepth())
                            commandException = (CommandException) e;
                    }

                    if (commandContext != null)
                        return commandContext;
                }

                if (entry.value().getRegisteredCommandVariants().isEmpty())
                    continue;

                return new CommandContext(
                        sender,
                        entry.value(),
                        args,
                        commandName,
                        new HashMap<>()
                );
            }

            CommandContext result = null;
            try {
                 result = commandOption.handle(
                        commandWrapper,
                        commandName,
                        args,
                        sender,
                        index
                );
            } catch (Throwable e) {
                if (!(e instanceof CommandException))
                    e = new CommandException(commandName, args, index, e.getMessage(), e);

                CommandException cmdException = (CommandException) e;

                if (commandException == null || commandException.getDepth() < cmdException.getDepth())
                    commandException = cmdException;
            }

            if (result == null)
                continue;

            return result;
        }

        if (commandException != null)
            throw commandException;

        return null;
    }

    /* package-private */ List<String> handleTabCompletion(RegisteredCommand commandWrapper, String commandName, String[] args, Sender sender, int index) {
        if (args.length <= index) return List.of();

        List<String> result = new ArrayList<>();
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            if (!entry.key().meetsRequirements(sender, commandName, args, index))
                continue;

            result.addAll(commandOption.handleTabCompletion(commandWrapper, commandName, args, sender, index));
        }

        return result;
    }

    /* package-private */ boolean haveAnyRequirementsMeet(Sender sender, String commandName, String[] args, int depth) {
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            try {
                if (entry.key().meetsRequirements(sender, commandName, args, depth))
                    return true;
            } catch (CommandException e) {
                continue;
            }

        }

        return false;
    }

}
