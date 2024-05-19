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

import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.commands.exception.UnknownCommandException;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.parsers.factory.ParserFactory;
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirement;
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirementFactory;
import net.apartium.cocoabeans.commands.requirements.RequirementFactory;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.NonExtendable
public abstract class CommandManager {

    public static final Set<ArgumentParser<?>> COMMON_PARSERS = Set.of(
            new IntParser(0),
            new LongParser(0),
            new FloatParser(0),
            new DoubleParser(0),
            new BooleanParser(0),
            new StringParser(0),
            new StringsParser(0)
    );


    protected final Map<String, RegisteredCommand> commandMap = new HashMap<>();
    private final ArgumentMapper argumentMapper;

    /* package-private */ final Map<Class<? extends ParserFactory>, ParserFactory> parserFactories = new HashMap<>();
    /* package-private */ final Map<Class<? extends ArgumentRequirementFactory>, ArgumentRequirementFactory> argumentRequirementFactories = new HashMap<>();
    /* package-private */ final Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories = new HashMap<>();

    /* package-private */ final Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();

    public CommandManager(ArgumentMapper argumentMapper) {
        this.argumentMapper = argumentMapper;
    }

    public void registerArgumentTypeHandler(ArgumentParser<?> argumentTypeHandler) {
        argumentTypeHandlerMap.put(argumentTypeHandler.getKeyword(), argumentTypeHandler);
    }

    public void registerArgumentTypeHandler(Set<ArgumentParser<?>> argumentTypeHandlers) {
        for (ArgumentParser<?> argumentTypeHandler : argumentTypeHandlers)
            registerArgumentTypeHandler(argumentTypeHandler);
    }


    public List<String> handleTabComplete(Sender sender, String invoke, String[] args) {
        RegisteredCommand registeredCommand = commandMap.get(invoke.toLowerCase());
        if (registeredCommand == null) return List.of();
        if (args.length == 0) args = new String[0];
        return registeredCommand.getCommandBranchProcessor().handleTabCompletion(registeredCommand, args, sender, 0);
    }


    public boolean handle(Sender sender, String commandName, String[] args) {
        RegisteredCommand registeredCommand = commandMap.get(commandName.toLowerCase());
        if (registeredCommand == null)
            throw new UnknownCommandException(commandName);

        CommandContext context = registeredCommand.getCommandBranchProcessor().handle(
                registeredCommand,
                commandName,
                args,
                sender,
                0
        );
        if (context == null) {
            boolean isNotMeetsRequirement = true;
            for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
                if (!listener.requirements().meetsRequirements(sender))
                    continue;

                isNotMeetsRequirement = false;
            }

            for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
                if (listener.listener().fallbackHandle(sender, commandName, args))
                    return true;

            }

            if (isNotMeetsRequirement) {
                sender.sendMessage("You don't have access to use this command!");
                return true;
            }

            return false;
        }

        for (RegisteredCommandVariant method : context.option().getRegisteredCommandVariants()) {
            if (invoke(context, sender, method))
                return true;
        }

        for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
            if (listener.listener().fallbackHandle(sender, commandName, args))
                return true;

        }

        return false;
    }

    private boolean invoke(CommandContext context, Sender sender, RegisteredCommandVariant registeredCommandVariant) {
        List<Object> parameters = argumentMapper.map(context, sender, registeredCommandVariant);

        for (int i = 0; i < registeredCommandVariant.parameters().length; i++) {
            Object obj = parameters.get(i + 1); // first element is class instance
            for (ArgumentRequirement argumentRequirement : registeredCommandVariant.parameters()[i].argumentRequirements()) {
                if (!argumentRequirement.meetsRequirement(sender, context, obj))
                    return false;
            }
        }

        Object output;
        try {
            output = registeredCommandVariant.method().invokeWithArguments(parameters);
        } catch (Throwable e) {
            Dispensers.dispense(e);
            return false; // never going to reach this place
        }

        if (output != null && output.getClass().equals(Boolean.class))
            return (boolean) output;

        return true;
    }

    public void addCommand(CommandNode commandNode) {
        if (commandNode == null) return;

        Class<?> c = commandNode.getClass();
        if (c.isAnnotation()) return;

        Command handler = c.getAnnotation(Command.class);
        if (handler == null)  return;


        RegisteredCommand registeredCommand = commandMap.computeIfAbsent(handler.value().toLowerCase(), (cmd) -> new RegisteredCommand(this));
        registeredCommand.addNode(commandNode);

        for (String alias : handler.aliases()) {
            commandMap.computeIfAbsent(alias.toLowerCase(), (cmd) -> new RegisteredCommand(this))
                    .addNode(commandNode);
        }

        addCommand(commandNode, handler);
    }

    protected abstract void addCommand(CommandNode commandNode, Command command);

}

