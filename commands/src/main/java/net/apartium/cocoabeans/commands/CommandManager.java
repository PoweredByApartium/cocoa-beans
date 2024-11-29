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
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.ExceptionArgumentMapper;
import net.apartium.cocoabeans.commands.exception.HandleExceptionVariant;
import net.apartium.cocoabeans.commands.exception.UnknownCommandResponse;
import net.apartium.cocoabeans.commands.lexer.CommandLexer;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.requirements.*;
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
    private final ExceptionArgumentMapper exceptionArgumentMapper;
    private final CommandLexer commandLexer;

    /* package-private */ final Map<Class<? extends ParserFactory>, ParserFactory> parserFactories = new HashMap<>();
    /* package-private */ final Map<Class<? extends ArgumentRequirementFactory>, ArgumentRequirementFactory> argumentRequirementFactories = new HashMap<>();
    /* package-private */ final Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories = new HashMap<>();

    /* package-private */ final Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();

    protected CommandManager(ArgumentMapper argumentMapper, ExceptionArgumentMapper exceptionArgumentMapper) {
        this(argumentMapper, exceptionArgumentMapper, new SimpleCommandLexer());
    }

    protected CommandManager(ArgumentMapper argumentMapper, ExceptionArgumentMapper exceptionArgumentMapper, CommandLexer commandLexer) {
        this.argumentMapper = argumentMapper;
        this.exceptionArgumentMapper = exceptionArgumentMapper;
        this.commandLexer = commandLexer;
    }

    public void registerArgumentTypeHandler(ArgumentParser<?> argumentTypeHandler) {
        argumentTypeHandlerMap.put(argumentTypeHandler.getKeyword(), argumentTypeHandler);
    }

    public void registerArgumentTypeHandler(Set<ArgumentParser<?>> argumentTypeHandlers) {
        for (ArgumentParser<?> argumentTypeHandler : argumentTypeHandlers)
            registerArgumentTypeHandler(argumentTypeHandler);
    }


    public List<String> handleTabComplete(Sender sender, String commandName, String[] args) {
        RegisteredCommand registeredCommand = commandMap.get(commandName.toLowerCase());
        if (registeredCommand == null) return List.of();
        if (args.length == 0) args = new String[0];
        return registeredCommand.getCommandBranchProcessor().handleTabCompletion(registeredCommand, commandName, args, sender, 0).stream().toList();
    }


    public boolean handle(Sender sender, String commandName, String[] args) throws Throwable {
        RegisteredCommand registeredCommand = commandMap.get(commandName.toLowerCase());
        if (registeredCommand == null)
            throw new UnknownCommandResponse(commandName).getError();

        CommandContext context = registeredCommand.getCommandBranchProcessor().handle(
                registeredCommand,
                commandName,
                args,
                sender,
                0
        );

        if (context == null) {
            BadCommandResponse badCommandResponse = null;
            for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {

                RequirementResult requirementResult = listener.requirements().meetsRequirements(new RequirementEvaluationContext(sender, commandName, args, 0));
                if (requirementResult.hasError()) {
                    badCommandResponse = requirementResult.getError();
                    break;
                }
            }


             // fall back will be called even if sender doesn't meet requirements
            for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
                if (listener.listener().fallbackHandle(sender, commandName, args))
                    return true;

            }

            if (badCommandResponse != null) {
                if (handleError(null, sender, commandName, args, registeredCommand, context.error().getError()))
                    return true;

                context.error().throwError();
                return false; // should never reach here
            }

            return false;
        }

        if (context.hasError()) {
            if (handleError(context, sender, commandName, args, registeredCommand, context.error().getError()))
                return true;

            context.error().throwError();
            return false; // should never reach here
        }
        

        for (RegisteredVariant method : context.option().getRegisteredCommandVariants()) {
            try {
                if (invoke(context, sender, method))
                    return true;
            } catch (Throwable e) {
                if (handleError(context, sender, commandName, args, registeredCommand, e)) return true;

                throw e;
            }
        }

        for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
            if (listener.listener().fallbackHandle(sender, commandName, args))
                return true;

        }

        return false;
    }

    private boolean handleError(CommandContext context, Sender sender, String commandName, String[] args, RegisteredCommand registeredCommand, Throwable error) {
        for (HandleExceptionVariant handleExceptionVariant : registeredCommand.getHandleExceptionVariants()) {
            if (invokeException(handleExceptionVariant, context, sender, commandName, args, error))
                return true;
        }

        for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
            if (listener.listener().handleException(sender, commandName, args, error))
                return true;
        }

        for (RegisteredCommand.RegisteredCommandNode listener : registeredCommand.getCommands()) {
            if (listener.listener().fallbackHandle(sender, commandName, args))
                return true;
        }
        return false;
    }

    private boolean invokeException(HandleExceptionVariant handleExceptionVariant, CommandContext context, Sender sender, String commandName, String[] args, Throwable throwable) {
        List<Object> parameters = exceptionArgumentMapper.map(handleExceptionVariant, context, sender, commandName, args, throwable);

        if (parameters == null)
            return false;

        Object output;
        try {
            output = handleExceptionVariant.method().invokeWithArguments(parameters);
        } catch (Throwable e) {
            Dispensers.dispense(e);
            return false; // never going to reach this place
        }

        if (output != null && output.getClass().equals(Boolean.class))
            return (boolean) output;

        return true;
    }

    private boolean invoke(CommandContext context, Sender sender, RegisteredVariant registeredVariant) {
        List<Object> parameters = new ArrayList<>(registeredVariant.argumentIndexList().stream()
                .<Object>map((argumentIndex -> argumentIndex.get(context.toArgumentContext())))
                .toList());

        parameters.add(0, registeredVariant.node());

        for (int i = 0; i < registeredVariant.parameters().length; i++) {
            Object obj = parameters.get(i + 1); // first element is class instance
            for (ArgumentRequirement argumentRequirement : registeredVariant.parameters()[i].argumentRequirements()) {
                if (!argumentRequirement.meetsRequirement(sender, context, obj))
                    return false;
            }
        }

        Object output;
        try {
            output = registeredVariant.method().invokeWithArguments(parameters);
        } catch (Throwable e) {
            Dispensers.dispense(e);
            return false; // never going to reach this place
        }

        if (output != null && output.getClass().equals(Boolean.class))
            return (boolean) output;

        return true;
    }

    public void addCommand(CommandNode commandNode) {
        if (commandNode == null)
            return;

        Class<? extends CommandNode> c = commandNode.getClass();
        Command handler = c.getAnnotation(Command.class);
        if (handler == null)
            return;


        RegisteredCommand registeredCommand = commandMap.computeIfAbsent(handler.value().toLowerCase(), (cmd) -> new RegisteredCommand(this));
        registeredCommand.addNode(commandNode);

        for (String alias : handler.aliases()) {
            commandMap.computeIfAbsent(alias.toLowerCase(), (cmd) -> new RegisteredCommand(this))
                    .addNode(commandNode);

        }

        addCommand(commandNode, handler);
    }

    public CommandInfo getCommandInfo(String commandName) {
        RegisteredCommand registeredCommand = commandMap.get(commandName.toLowerCase());
        if (registeredCommand == null)
            return null;

        return registeredCommand.getCommandInfo();
    }

    protected abstract void addCommand(CommandNode commandNode, Command command);

    public ArgumentMapper getArgumentMapper() {
        return argumentMapper;
    }

    public ExceptionArgumentMapper getExceptionArgumentMapper() {
        return exceptionArgumentMapper;
    }

    public CommandLexer getCommandLexer() {
        return commandLexer;
    }
}

