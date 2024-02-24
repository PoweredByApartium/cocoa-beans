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

import net.apartium.cocoabeans.commands.requirements.RequirementSet;
import net.apartium.cocoabeans.structs.Entry;
import net.apartium.cocoabeans.structs.LinkedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* package-private */ class CommandBranchProcessor {
    /* package-private */ final LinkedList<Entry<RequirementSet, CommandOption>> objectMap = new LinkedList<>();

    /* package-private */ @Nullable CommandContext handle(RegisteredCommand commandWrapper, String commandName, String[] args, Sender sender, int index) {
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            if (!entry.key().meetsRequirements(sender))
                continue;

            if (args.length == index) {
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

            CommandContext result = commandOption.handle(
                    commandWrapper,
                    commandName,
                    args,
                    sender,
                    index
            );

            if (result == null)
                continue;

            return result;
        }

        return null;
    }

    /* package-private */ List<String> handleTabCompletion(RegisteredCommand commandWrapper, String[] args, Sender sender, int index) {
        if (args.length <= index) return List.of();

        List<String> result = new ArrayList<>();
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            if (!entry.key().meetsRequirements(sender))
                continue;

            result.addAll(commandOption.handleTabCompletion(commandWrapper, args, sender, index));
        }

        return result;
    }

    /* package-private */ boolean haveAnyRequirementsMeet(Sender sender) {
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            if (entry.key().meetsRequirements(sender))
                return true;

        }

        return false;
    }

}
