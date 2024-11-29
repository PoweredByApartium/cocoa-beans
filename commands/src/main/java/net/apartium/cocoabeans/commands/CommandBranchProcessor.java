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
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import net.apartium.cocoabeans.commands.requirements.RequirementSet;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/* package-private */ class CommandBranchProcessor {

    private final CommandManager commandManager;

    /* package-private */ final List<Entry<RequirementSet, CommandOption>> objectMap = new ArrayList<>();

    CommandBranchProcessor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /* package-private */ @Nullable CommandContext handle(RegisteredCommand commandWrapper, String commandName, String[] args, Sender sender, int index) {

        BadCommandResponse commandError = null;

        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            RequirementResult requirementResult = entry.key().meetsRequirements(new RequirementEvaluationContext(sender, commandName, args, index));

            if (requirementResult.hasError()) {
                if (commandError == null || commandError.getDepth() < requirementResult.getError().getDepth())
                    commandError = requirementResult.getError();
                continue;
            }

            if (!requirementResult.meetRequirement())
                continue;

            if (args.length <= index) {
                if (!entry.value().getOptionalArgumentTypeHandlerMap().isEmpty()) {
                    CommandContext commandContext = entry.value().handleOptional(commandWrapper, commandName, args, sender, index);

                    if (commandContext == null)
                        continue;

                    if (commandContext.hasError()) {
                        if (commandError == null || commandError.getDepth() < commandContext.error().getDepth())
                            commandError = commandContext.error();

                        continue;
                    }

                    addToParserArgs(requirementResult, commandContext);
                    return commandContext;
                }

                if (entry.value().getRegisteredCommandVariants().isEmpty())
                    continue;

                CommandContext commandContext = new CommandContext(
                        sender,
                        entry.value().getCommandInfo(),
                        entry.value(),
                        null,
                        args,
                        commandName,
                        new HashMap<>()
                );

                addToParserArgs(requirementResult, commandContext);

                return commandContext;
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

            if (result.hasError()) {
                if (commandError == null || commandError.getDepth() < result.error().getDepth())
                    commandError = result.error();
                continue;
            }

            addToParserArgs(requirementResult, result);

            return result;
        }

        if (commandError != null)
            return new CommandContext(sender, null, null, commandError, args, commandName, new HashMap<>());

        return null;
    }

    private void addToParserArgs(RequirementResult requirementResult, CommandContext context) {
        for (RequirementResult.Value value : requirementResult.getValues()) {
            context.parsedArgs()
                    .computeIfAbsent(value.clazz(), (clazz) -> new ArrayList<>())
                    .add(value.value());
        }
    }

    /* package-private */ Set<String> handleTabCompletion(RegisteredCommand commandWrapper, String commandName, String[] args, Sender sender, int index) {
        if (args.length <= index) return Set.of();

        Set<String> result = new HashSet<>();
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            if (!entry.key().meetsRequirements(new RequirementEvaluationContext(sender, commandName, args, index)).meetRequirement())
                continue;

            result.addAll(commandOption.handleTabCompletion(commandWrapper, commandName, args, sender, index));
        }

        return result;
    }

    /**
     * For tab completion only
     *
     * @param sender the sender
     * @param commandName the command name
     * @param args args
     * @param depth depth
     * @return true if meets any requirements, false if not
     */
    /* package-private */ boolean haveAnyRequirementsMeet(Sender sender, String commandName, String[] args, int depth) {
        for (Entry<RequirementSet, CommandOption> entry : objectMap) {
            CommandOption commandOption = entry.value();
            if (commandOption == null)
                continue;

            if (entry.key().meetsRequirements(new RequirementEvaluationContext(sender, commandName, args, depth)).meetRequirement())
                    return true;

        }

        return false;
    }

}
