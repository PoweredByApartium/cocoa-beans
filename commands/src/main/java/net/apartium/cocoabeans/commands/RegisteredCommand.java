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

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.exception.HandleExceptionVariant;
import net.apartium.cocoabeans.commands.exception.UnknownTokenException;
import net.apartium.cocoabeans.commands.lexer.ArgumentParserToken;
import net.apartium.cocoabeans.commands.lexer.CommandToken;
import net.apartium.cocoabeans.commands.lexer.KeywordToken;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.reflect.ClassUtils;
import net.apartium.cocoabeans.reflect.MethodUtils;
import net.apartium.cocoabeans.structs.Entry;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import static net.apartium.cocoabeans.commands.RegisteredVariant.REGISTERED_VARIANT_COMPARATOR;

/*package-private*/ class RegisteredCommand {

    private static final Comparator<HandleExceptionVariant> HANDLE_EXCEPTION_VARIANT_COMPARATOR = (a, b) -> Integer.compare(b.priority(), a.priority());

    public record RegisteredCommandNode(CommandNode listener, RequirementSet requirements) {}

    private final CommandManager commandManager;

    private final List<RegisteredCommandNode> commands = new ArrayList<>();
    private final List<HandleExceptionVariant> handleExceptionVariants = new ArrayList<>();
    private final CommandBranchProcessor commandBranchProcessor;
    private final CommandInfo commandInfo = new CommandInfo();


    RegisteredCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        commandBranchProcessor = new CommandBranchProcessor(commandManager);
    }

    public void addNode(CommandNode node) {
        Class<?> clazz = node.getClass();

        commandInfo.fromAnnotations(clazz.getAnnotations(), false);

        RequirementSet requirementSet = new RequirementSet(findAllRequirements(node, clazz));

        Method fallbackHandle;
        try {
            fallbackHandle = clazz.getMethod("fallbackHandle", Sender.class, String.class, String[].class);
        } catch (Exception e) {
            throw new RuntimeException("What is going on here", e);
        }

        this.commands.add(new RegisteredCommandNode(
                node,
                new RequirementSet(
                        requirementSet,
                        RequirementFactory.createRequirementSet(node, fallbackHandle.getAnnotations(), commandManager.requirementFactories)
                ))
        );
        Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

        // Add class parsers & class parsers
        CollectionHelpers.mergeInto(
                argumentTypeHandlerMap,
                ParserFactory.findClassParsers(node, clazz, commandManager.parserFactories)
        );

        // Add command manager argument parsers
        CollectionHelpers.mergeInto(
                argumentTypeHandlerMap,
                commandManager.argumentTypeHandlerMap
        );

        List<Requirement> classRequirementsResult = new ArrayList<>();
        CommandOption commandOption = createCommandOption(requirementSet, commandBranchProcessor, classRequirementsResult);

        for (Method method : clazz.getMethods()) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

            for (SubCommand subCommand : subCommands) {
                try {
                    parseSubCommand(new ParserSubCommandContext(method , subCommand, clazz, node, commandOption, argumentTypeHandlerMap, requirementSet), publicLookup, new ArrayList<>(), new ArrayList<>(classRequirementsResult));
                } catch (IllegalAccessException e) {
                    Dispensers.dispense(e);
                    return;
                }
            }

            serializeExceptionHandles(method, node, publicLookup);


            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
                try {
                    handleSubCommand(new ParserSubCommandContext(
                            method,
                            null,
                            clazz,
                            node,
                            commandOption,
                            argumentTypeHandlerMap,
                            requirementSet
                    ), publicLookup, targetMethod, classRequirementsResult);
                } catch (IllegalAccessException e) {
                    Dispensers.dispense(e);
                    return;
                }
            }

        }

    }

    private void serializeExceptionHandles(Method method, CommandNode node, MethodHandles.Lookup publicLookup) {
        for (Method targetMethod : Stream.concat(
                Stream.of(method),
                MethodUtils.getMethodsFromSuperClassAndInterface(method).stream()
        ).toList()) {
            ExceptionHandle exceptionHandle = targetMethod.getAnnotation(ExceptionHandle.class);
            if (exceptionHandle == null)
                continue;

            try {
                CollectionHelpers.addElementSorted(
                        handleExceptionVariants,
                        new HandleExceptionVariant(
                                publicLookup.unreflect(method),
                                Arrays.stream(method.getParameters()).map(Parameter::getType).toArray(Class[]::new),
                                node,
                                exceptionHandle.priority()
                        ),
                        HANDLE_EXCEPTION_VARIANT_COMPARATOR
                );
            } catch (IllegalAccessException e) {
                Dispensers.dispense(e);
                return;
            }
        }

    }

    private void handleSubCommand(ParserSubCommandContext context, MethodHandles.Lookup publicLookup, Method targetMethod, List<Requirement> classRequirementsResult) throws IllegalAccessException {
        ExceptionHandle exceptionHandle;
        if (targetMethod == null)
            return;

        SubCommand[] superSubCommands = targetMethod.getAnnotationsByType(SubCommand.class);

        for (SubCommand subCommand : superSubCommands) {
            parseSubCommand(new ParserSubCommandContext(context.method, subCommand, context.clazz, context.commandNode, context.commandOption, context.argumentTypeHandlerMap, context.requirementSet), publicLookup, new ArrayList<>(), new ArrayList<>(classRequirementsResult));
        }

        exceptionHandle = targetMethod.getAnnotation(ExceptionHandle.class);
        if (exceptionHandle != null) {
            try {
                CollectionHelpers.addElementSorted(
                        handleExceptionVariants,
                        new HandleExceptionVariant(
                                publicLookup.unreflect(context.method),
                                Arrays.stream(context.method.getParameters()).map(Parameter::getType).toArray(Class[]::new),
                                context.commandNode,
                                exceptionHandle.priority()
                        ),
                        HANDLE_EXCEPTION_VARIANT_COMPARATOR
                );
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void parseSubCommand(ParserSubCommandContext context, MethodHandles.Lookup publicLookup, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult) throws IllegalAccessException {
        if (context.subCommand == null)
            return;

        if (!Modifier.isPublic(context.method.getModifiers()))
            return;

        if (Modifier.isStatic(context.method.getModifiers()))
            throw new IllegalAccessException("Static method " + context.clazz.getName() + "#" + context.method.getName() + " is not supported");


        Map<String, ArgumentParser<?>> methodArgumentTypeHandlerMap = new HashMap<>(ParserFactory.getArgumentParsers(context.commandNode, context.method.getAnnotations(), context.method, false, commandManager.parserFactories));

        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(context.method)) {
            CollectionHelpers.mergeInto(
                    methodArgumentTypeHandlerMap,
                    ParserFactory.getArgumentParsers(context.commandNode, targetMethod.getAnnotations(), targetMethod, false, commandManager.parserFactories)
            );
        }

        CollectionHelpers.mergeInto(
                methodArgumentTypeHandlerMap,
                context.argumentTypeHandlerMap
        );

        CommandInfo methodInfo = generateCommandInfo(context.method);

        RequirementSet methodRequirements = new RequirementSet(
                findAllRequirements(context.commandNode, context.method),
                context.requirementSet
        );


        String[] split = context.subCommand.value().split("\\s+");
        if (isEmptyArgs(split)) {
            CommandOption cmdOption = createCommandOption(methodRequirements, commandBranchProcessor, requirementsResult);

            cmdOption.getCommandInfo().fromCommandInfo(methodInfo);
            RegisteredVariant.Parameter[] parameters = RegisteredVariant.Parameter.of(context.commandNode, context.method.getParameters(), commandManager.argumentRequirementFactories);

            try {
                CollectionHelpers.addElementSorted(
                        cmdOption.getRegisteredCommandVariants(),
                        new RegisteredVariant(
                            publicLookup.unreflect(context.method),
                            parameters,
                            context.commandNode,
                            commandManager.getArgumentMapper().mapIndices(parameters, parsersResult, requirementsResult),
                            context.subCommand.priority()
                        ),
                        REGISTERED_VARIANT_COMPARATOR
                );
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing method", e);
            }

            return;
        }

        CommandOption currentCommandOption = context.commandOption;
        List<CommandToken> tokens = commandManager.getCommandLexer().tokenize(context.subCommand.value());

        for (int i = 0; i < tokens.size(); i++) {
            CommandToken token = tokens.get(i);

            //  TODO may need to split requirements so it will be faster and joined stuff
            RequirementSet requirements = i == 0 ? methodRequirements : new RequirementSet();

            if (token instanceof KeywordToken keywordToken) {
                currentCommandOption = createKeywordOption(currentCommandOption, context.subCommand, keywordToken, requirements, requirementsResult);
                continue;
            }

            if (token instanceof ArgumentParserToken argumentParserToken) {
                currentCommandOption = createArgumentOption(currentCommandOption, argumentParserToken, methodArgumentTypeHandlerMap, requirements, parsersResult, requirementsResult);
                continue;
            }

            throw new UnknownTokenException(token);
        }


        currentCommandOption.getCommandInfo().fromCommandInfo(methodInfo);

        RegisteredVariant.Parameter[] parameters = RegisteredVariant.Parameter.of(context.commandNode, context.method.getParameters(), commandManager.argumentRequirementFactories);

        CollectionHelpers.addElementSorted(
                currentCommandOption.getRegisteredCommandVariants(),
                new RegisteredVariant(
                        publicLookup.unreflect(context.method),
                        parameters,
                        context.commandNode,
                        commandManager.getArgumentMapper().mapIndices(parameters, parsersResult, requirementsResult),
                        context.subCommand.priority()
                ),
                REGISTERED_VARIANT_COMPARATOR
        );
    }

    private boolean isEmptyArgs(String[] split) {
        return split.length == 0 || split.length == 1 && split[0].isEmpty();
    }

    private CommandInfo generateCommandInfo(Method method) {
        CommandInfo info = new CommandInfo();

        info.fromAnnotations(method.getAnnotations(), true);
        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
            info.fromAnnotations(targetMethod.getAnnotations(), false);

        return info;
    }

    private CommandOption createKeywordOption(CommandOption currentCommandOption, SubCommand subCommand, KeywordToken keywordToken, RequirementSet requirements, List<Requirement> requirementsResult) {
        Map<String, CommandBranchProcessor> keywordMap = subCommand.ignoreCase()
                ? currentCommandOption.getKeywordIgnoreCaseMap()
                : currentCommandOption.getKeywordMap();

        String keyword = subCommand.ignoreCase()
                ? keywordToken.getKeyword().toLowerCase()
                : keywordToken.getKeyword();

        CommandBranchProcessor branchProcessor = keywordMap.computeIfAbsent(keyword, key -> new CommandBranchProcessor(commandManager));
        return createCommandOption(requirements, branchProcessor, requirementsResult);
    }

    private CommandOption createArgumentOption(CommandOption currentCommandOption, ArgumentParserToken argumentParserToken, Map<String, ArgumentParser<?>> parserMap, RequirementSet requirements, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult) {
        RegisterArgumentParser<?> parser = argumentParserToken.getParser(parserMap);
        if (parser == null)
            throw new IllegalArgumentException("Parser not found: " + argumentParserToken.getParserName());

        Entry<RegisterArgumentParser<?>, CommandBranchProcessor> entryArgument = currentCommandOption.getArgumentTypeHandlerMap().stream()
                .filter(entry -> entry.key().equals(parser))
                .findAny()
                .orElse(null);

        CommandBranchProcessor commandBranchProcessor = entryArgument == null ? null : entryArgument.value();

        if (commandBranchProcessor == null) {
            commandBranchProcessor = new CommandBranchProcessor(commandManager);
            CollectionHelpers.addElementSorted(
                    currentCommandOption.getArgumentTypeHandlerMap(),
                    new Entry<>(
                            parser,
                            commandBranchProcessor
                    ),
                    (a, b) -> b.key().compareTo(a.key())
            );
        }

        parsersResult.add(entryArgument == null ? parser : entryArgument.key());

        if (parser.isOptional()) {
            CommandBranchProcessor branchProcessor = currentCommandOption.getOptionalArgumentTypeHandlerMap().stream()
                    .filter(entry -> entry.key().equals(parser))
                    .findAny()
                    .map(Entry::value)
                    .orElse(null);

            if (branchProcessor == null) {
                branchProcessor = commandBranchProcessor;
                CollectionHelpers.addElementSorted(
                        currentCommandOption.getOptionalArgumentTypeHandlerMap(),
                        new Entry<>(
                                parser,
                                branchProcessor
                        ),
                        (a, b) -> b.key().compareTo(a.key())
                );
            }
        }

        return createCommandOption(requirements, commandBranchProcessor, requirementsResult);
    }

    private CommandOption createCommandOption(RequirementSet requirements, CommandBranchProcessor branchProcessor, List<Requirement> requirementsResult) {
        CommandOption cmdOption = branchProcessor.objectMap.stream()
                .filter(entry -> entry.key().equals(requirements))
                .findAny()
                .map(Entry::value)
                .orElse(null);

        if (cmdOption == null) {
            cmdOption = new CommandOption(commandManager);
            branchProcessor.objectMap.add(new Entry<>(
                    requirements,
                    cmdOption
            ));
        }

        requirementsResult.addAll(requirements);

        return cmdOption;
    }

    private Set<Requirement> findAllRequirements(CommandNode commandNode, Class<?> clazz) {
        Set<Requirement> requirements = new HashSet<>();

        for (Class<?> c : ClassUtils.getSuperClassAndInterfaces(clazz)) {
            requirements.addAll(RequirementFactory.createRequirementSet(commandNode, c.getAnnotations(), commandManager.requirementFactories));
        }

        return requirements;
    }

    private Set<Requirement> findAllRequirements(CommandNode commandNode, Method method) {
        Set<Requirement> requirements = new HashSet<>(RequirementFactory.createRequirementSet(commandNode, method.getAnnotations(), commandManager.requirementFactories));
        for (Method target : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            requirements.addAll(RequirementFactory.createRequirementSet(commandNode, target.getAnnotations(), commandManager.requirementFactories));
        }

        return requirements;
    }

     record ParserSubCommandContext(
            Method method,
            SubCommand subCommand,
            Class<?> clazz,
            CommandNode commandNode,
            CommandOption commandOption,
            Map<String, ArgumentParser<?>> argumentTypeHandlerMap,
            RequirementSet requirementSet
    ) {

    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    public List<RegisteredCommandNode> getCommands() {
        return commands;
    }

    public CommandBranchProcessor getCommandBranchProcessor() {
        return commandBranchProcessor;
    }

    public Iterable<HandleExceptionVariant> getHandleExceptionVariants() {
        return handleExceptionVariants;
    }
}
