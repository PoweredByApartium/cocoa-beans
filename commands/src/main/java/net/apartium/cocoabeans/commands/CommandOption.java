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
import net.apartium.cocoabeans.commands.exception.InvalidUsageException;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/* package-private */ class CommandOption {

    private final CommandManager commandManager;

    private final List<RegisteredCommandVariant> registeredCommandVariants = new ArrayList<>();

    private final Map<String, CommandBranchProcessor> keywordIgnoreCaseMap = new HashMap<>();
    private final Map<String, CommandBranchProcessor> keywordMap = new HashMap<>();
    private final List<Entry<ArgumentParser<?>, CommandBranchProcessor>> argumentTypeHandlerMap = new ArrayList<>();
    private final List<Entry<OptionalArgumentParser<?>, CommandBranchProcessor>> argumentTypeOptionalHandlerMap = new ArrayList<>();

    CommandOption(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public CommandContext handle(RegisteredCommand registeredCommand, String commandName, String[] args, Sender sender, int index) {
        if (args.length == 0 && index == 0) {
            if (!registeredCommandVariants.isEmpty() && argumentTypeOptionalHandlerMap.isEmpty())
                return new CommandContext(
                    sender,
                    this,
                    args,
                    commandName,
                    new HashMap<>()
                );

            try {
                return handleOptional(registeredCommand, commandName, args, sender, index);
            } catch (Throwable throwable) {
                throw throwable;
            }
        }


        if (args.length <= index) {
            try {
                return handleOptional(registeredCommand, commandName, args, sender, index);
            } catch (Throwable throwable) {
                throw throwable;
            }
        }

        CommandException commandException = null;
        CommandBranchProcessor commandBranchProcessor = keywordMap.get(args[index]);
        if (commandBranchProcessor == null)
            commandBranchProcessor = keywordIgnoreCaseMap.get(args[index].toLowerCase());

        if (commandBranchProcessor != null) {
            CommandContext result = null;
            try {
                 result = commandBranchProcessor.handle(
                        registeredCommand,
                        commandName,
                        args,
                        sender,
                        index + 1
                );
            } catch (Throwable throwable) {
                if (!(throwable instanceof CommandException))
                    throwable = new CommandException(commandName, args, index, throwable.getMessage(), throwable);

                commandException = (CommandException) throwable;
            }

            if (result != null)
                return result;
        }

        for (Entry<ArgumentParser<?>, CommandBranchProcessor> entry : argumentTypeHandlerMap) {
            ArgumentParser<?> typeParser = entry.key();
            Optional<? extends ArgumentParser.ParseResult<?>> parse = typeParser.parse(new AbstractCommandProcessingContext(sender, args, index));

            if (parse.isEmpty()) {
                if (typeParser instanceof OptionalArgumentParser<?> optionalArgumentParser) {
                    if (!optionalArgumentParser.optionalNotMatch())
                        return null;

                    CommandContext result = null;

                    try {
                        result = entry.value().handle(
                                registeredCommand,
                                commandName,
                                args,
                                sender,
                                index + 1
                        );
                    } catch (Throwable throwable) {
                        if (!(throwable instanceof CommandException))
                            throwable = new CommandException(commandName, args, index, throwable.getMessage(), throwable);

                        if (commandException == null || commandException.getDepth() < ((CommandException) throwable).getDepth())
                            commandException = (CommandException) throwable;
                    }

                    if (result == null)
                        continue;

                    result.parsedArgs()
                            .computeIfAbsent(typeParser.getArgumentType(), (clazz) -> new ArrayList<>())
                            .add(0, Optional.empty());

                    return result;
                }
                continue;
            }

            int newIndex = parse.get().newIndex();

            if (newIndex <= index)
                throw new RuntimeException("There is an exception with " + typeParser.getClass().getName() + " return new index that isn't bigger than current index");

            CommandContext result = null;

            try {
                result = entry.value().handle(
                        registeredCommand,
                        commandName,
                        args,
                        sender,
                        newIndex
                );
            } catch (Throwable throwable) {
                if (!(throwable instanceof CommandException))
                    throwable = new CommandException(commandName, args, index, throwable.getMessage(), throwable);

                if (commandException == null || commandException.getDepth() < ((CommandException) throwable).getDepth())
                    commandException = (CommandException) throwable;
            }

            if (result == null)
                continue;

            result.parsedArgs()
                    .computeIfAbsent(typeParser.getArgumentType(), (clazz) -> new ArrayList<>())
                    .add(0, parse.get().result());

            return result;
        }

        if (commandException != null)
            throw commandException;

        throw new InvalidUsageException(commandName, args, index);
    }

    @Nullable
    /* package-private */ CommandContext handleOptional(RegisteredCommand registeredCommand, String commandName, String[] args, Sender sender, int index) {
        for (var entry : argumentTypeOptionalHandlerMap) {
            CommandContext result = entry.value().handle(
                    registeredCommand,
                    commandName,
                    args,
                    sender,
                    index + 1
            );


            if (result == null) {
                if (!registeredCommandVariants.isEmpty())
                    return new CommandContext(
                            sender,
                            this,
                            args,
                            commandName,
                            new HashMap<>()
                    );

                continue;
            }

            result.parsedArgs()
                    .computeIfAbsent(entry.key().parser().getArgumentType(), (clazz) -> new ArrayList<>())
                    .add(0, Optional.empty());

            return result;
        }

        return null;
    }

    public List<String> handleTabCompletion(RegisteredCommand registeredCommand, String commandName, String[] args, Sender sender, int index) {
        if (args.length <= index)
            return List.of();

        if (args.length - 1 == index) {
            List<String> result = new ArrayList<>();

            for (var entry : keywordMap.entrySet()) {
                if (!entry.getKey().startsWith(args[index]))
                    continue;

                if (!entry.getValue().haveAnyRequirementsMeet(sender, commandName, args, index))
                    continue;

                result.add(entry.getKey());
            }

            for (var entry : keywordIgnoreCaseMap.entrySet()) {
                if (!entry.getKey().startsWith(args[index].toLowerCase()))
                    continue;

                if (!entry.getValue().haveAnyRequirementsMeet(sender, commandName, args, index))
                    continue;

                result.add(entry.getKey());
            }

            for (Entry<ArgumentParser<?>, CommandBranchProcessor> entry : argumentTypeHandlerMap) {
                if (!entry.value().haveAnyRequirementsMeet(sender, commandName, args, index))
                    continue;

                Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = entry.key().tabCompletion(new AbstractCommandProcessingContext(sender, args, index));
                if (tabCompletionResult.isEmpty()) {
                    if (entry.key() instanceof OptionalArgumentParser<?> optionalArgumentParser) {
                        if (!optionalArgumentParser.optionalNotMatch())
                            continue;

                        result.addAll(entry.value().handleTabCompletion(registeredCommand, commandName, args, sender, index + 1));
                    }

                    continue;
                }

                result.addAll(tabCompletionResult.get().result());
            }

            return result;
        }

        List<String> result = new ArrayList<>();
        CommandBranchProcessor commandBranchProcessor = keywordMap.get(args[index]);
        if (commandBranchProcessor != null) {
            List<String> strings = commandBranchProcessor.handleTabCompletion(
                    registeredCommand,
                    commandName,
                    args,
                    sender,
                    index + 1
            );
            if (!strings.isEmpty())
                result.addAll(strings);
        }

        commandBranchProcessor = keywordIgnoreCaseMap.get(args[index].toLowerCase());
        if (commandBranchProcessor != null) {
            List<String> strings = commandBranchProcessor.handleTabCompletion(
                    registeredCommand,
                    commandName,
                    args,
                    sender,
                    index + 1
            );

            if (!strings.isEmpty())
                result.addAll(strings);
        }

        for (Entry<ArgumentParser<?>, CommandBranchProcessor> entry : argumentTypeHandlerMap) {
            ArgumentParser<?> typeParser = entry.key();
            OptionalInt parse = typeParser.tryParse(new AbstractCommandProcessingContext(sender, args, index));
            if (parse.isEmpty()) {
                if (!entry.value().haveAnyRequirementsMeet(sender, commandName, args, index))
                    continue;

                Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = entry.key().tabCompletion(new AbstractCommandProcessingContext(sender, args, index));
                if (tabCompletionResult.isEmpty()) {
                    if (entry.key() instanceof OptionalArgumentParser<?> optionalArgumentParser) {
                        if (!optionalArgumentParser.optionalNotMatch())
                            continue;

                        result.addAll(entry.value().handleTabCompletion(registeredCommand, commandName, args, sender, index + 1));
                    }

                    continue;
                }

                if (tabCompletionResult.get().newIndex() < args.length)
                    continue;

                result.addAll(tabCompletionResult.get().result());
                continue;
            }

            if (parse.getAsInt() <= args.length) {
                if (entry.value().haveAnyRequirementsMeet(sender, commandName, args, index)) {
                    Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = entry.key().tabCompletion(new AbstractCommandProcessingContext(sender, args, index));
                    if (tabCompletionResult.isPresent()) {
                        if (tabCompletionResult.get().newIndex() >= args.length) {
                            result.addAll(tabCompletionResult.get().result().stream().toList());
                            continue;
                        }
                    }
                }
            }

            int newIndex = parse.getAsInt();

            if (newIndex <= index)
                throw new RuntimeException("There is an exception with " + typeParser.getClass().getName() + " return new index that isn't bigger then current index");

            List<String> strings = entry.value().handleTabCompletion(registeredCommand, commandName, args, sender, newIndex);
            if (strings.isEmpty())
                continue;

            result.addAll(strings);
        }

        return result;
    }

    public List<RegisteredCommandVariant> getRegisteredCommandVariants() {
        return registeredCommandVariants;
    }

    public List<Entry<ArgumentParser<?>, CommandBranchProcessor>> getArgumentTypeHandlerMap() {
        return argumentTypeHandlerMap;
    }

    public List<Entry<OptionalArgumentParser<?>, CommandBranchProcessor>> getOptionalArgumentTypeHandlerMap() {
        return argumentTypeOptionalHandlerMap;
    }

    public Map<String, CommandBranchProcessor> getKeywordIgnoreCaseMap() {
        return keywordIgnoreCaseMap;
    }

    public Map<String, CommandBranchProcessor> getKeywordMap() {
        return keywordMap;
    }

}
