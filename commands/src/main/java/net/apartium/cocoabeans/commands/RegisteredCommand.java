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
import net.apartium.cocoabeans.commands.exception.*;
import net.apartium.cocoabeans.commands.lexer.ArgumentParserToken;
import net.apartium.cocoabeans.commands.lexer.CommandToken;
import net.apartium.cocoabeans.commands.lexer.KeywordToken;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandDefinition;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandVariant;
import net.apartium.cocoabeans.reflect.ClassUtils;
import net.apartium.cocoabeans.reflect.MethodUtils;
import net.apartium.cocoabeans.structs.Entry;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.apartium.cocoabeans.commands.RegisteredVariant.REGISTERED_VARIANT_COMPARATOR;

/*package-private*/ class RegisteredCommand {

    private static final Comparator<HandleExceptionVariant> HANDLE_EXCEPTION_VARIANT_COMPARATOR = (a, b) -> Integer.compare(b.priority(), a.priority());

    public record RegisteredCommandNode(CommandNode listener, RequirementSet requirements) {}
    public record VirtualCommandNode(VirtualCommandDefinition command, Function<CommandContext, Boolean> callback) {}

    private final CommandManager commandManager;

    private final List<RegisteredCommandNode> commands = new ArrayList<>();
    private final List<VirtualCommandNode> virtualNodes = new ArrayList<>();

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
                        RequirementFactory.createRequirementSet(node, fallbackHandle.getAnnotations(), commandManager.requirementFactories, commandManager.getExternalRequirementFactories())
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

        for (Method method : MethodUtils.getAllMethods(clazz)) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

            for (SubCommand subCommand : subCommands) {
                try {
                    parseSubCommand(new ParserSubCommandContext(method , subCommand, clazz, node, commandOption, argumentTypeHandlerMap, requirementSet), publicLookup, new ArrayList<>(), new ArrayList<>(classRequirementsResult));
                } catch (IllegalAccessException e) {
                    Dispensers.dispense(e);
                    return;
                }
            }

            try {
                serializeExceptionHandles(method, node, publicLookup);
            } catch (IllegalAccessException e) {
                Dispensers.dispense(e);
                return;
            }


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

    private RequirementSet getVirtualRequirement(Map<String, Object> metadata) {
        Set<Requirement> requirements = new HashSet<>();

        for (Function<Map<String, Object>, Set<Requirement>> metadataHandler : commandManager.getMetadataHandlers()) {
            Set<Requirement> apply = metadataHandler.apply(metadata);
            if (apply != null)
                requirements.addAll(apply);
        }

        return new RequirementSet(requirements);
    }

    public void addVirtualCommand(VirtualCommandDefinition virtualCommandDefinition, Function<CommandContext, Boolean> callback, ArgumentParser<?> fallbackParser) {
        commandInfo.fromCommandInfo(virtualCommandDefinition.info());

        this.virtualNodes.add(new VirtualCommandNode(virtualCommandDefinition, callback));

        List<Requirement> classRequirementsResult = new ArrayList<>();
        CommandOption virtualOption = createCommandOption(
                getVirtualRequirement(virtualCommandDefinition.metadata()),
                this.commandBranchProcessor,
                classRequirementsResult
        );

        List<RegisterArgumentParser<?>> parsersResult = new ArrayList<>();
        for (VirtualCommandVariant variant : virtualCommandDefinition.variants()) {
            List<CommandToken> tokens = commandManager.getCommandLexer().tokenize(variant.variant());

            CommandOption currentOption = virtualOption;
            RequirementSet methodRequirements = getVirtualRequirement(variant.metadata());

            for (int i = 0; i < tokens.size(); i++) {
                CommandToken token = tokens.get(i);

                if (token instanceof KeywordToken keywordToken) {
                    currentOption = createKeywordOption(
                            currentOption,
                            variant.ignoreCase(),
                            keywordToken,
                            resolveRequirementsForBranch(i, methodRequirements),
                            classRequirementsResult
                    );
                    continue;
                }

                if (token instanceof ArgumentParserToken argumentParserToken) {
                    currentOption = createArgumentOption(
                            currentOption,
                            argumentParserToken,
                            commandManager.argumentTypeHandlerMap,
                            resolveRequirementsForBranch(i, methodRequirements),
                            parsersResult,
                            classRequirementsResult,
                            fallbackParser
                    );
                    continue;
                }

                throw new UnknownTokenException(token);
            }
        }
    }

    private void serializeExceptionHandles(Method method, CommandNode node, MethodHandles.Lookup publicLookup) throws IllegalAccessException {
        for (Method targetMethod : Stream.concat(
                Stream.of(method),
                MethodUtils.getMethodsFromSuperClassAndInterface(method).stream()
        ).toList()) {
            ExceptionHandle exceptionHandle = targetMethod.getAnnotation(ExceptionHandle.class);
            if (exceptionHandle == null)
                continue;

            if (!Modifier.isPublic(method.getModifiers()))
                throw new IllegalAccessException("Method " + method.getName() + "#" + node.getClass().getSimpleName() + " is not public");

            CollectionHelpers.addElementSorted(
                    handleExceptionVariants,
                    getHandleExceptionVariant(node, method, exceptionHandle, publicLookup),
                    HANDLE_EXCEPTION_VARIANT_COMPARATOR
            );
        }

    }

    private HandleExceptionVariant getHandleExceptionVariant(GenericNode node, Method method, ExceptionHandle exceptionHandle, MethodHandles.Lookup publicLookup) {
        RegisteredVariant.Parameter[] parameters = RegisteredVariant.Parameter.of(node, method.getParameters(), commandManager.argumentRequirementFactories);

        List<Class<?>> additionalTypes = new ArrayList<>(List.of(
                exceptionHandle.value()
        ));

        try {
            return new HandleExceptionVariant(
                    exceptionHandle.value(),
                    publicLookup.unreflect(method),
                    Arrays.stream(method.getParameters()).map(Parameter::getType).toArray(Class[]::new),
                    node,
                    commandManager.getArgumentMapper().mapIndices(parameters, List.of(), List.of(), additionalTypes),
                    exceptionHandle.priority()
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
            CollectionHelpers.addElementSorted(
                    handleExceptionVariants,
                    getHandleExceptionVariant(context.commandNode, context.method, exceptionHandle, publicLookup),
                    HANDLE_EXCEPTION_VARIANT_COMPARATOR
            );
        }
    }

    private void parseSubCommand(ParserSubCommandContext context, MethodHandles.Lookup publicLookup, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult) throws IllegalAccessException {
        if (context.subCommand == null)
            return;

        if (!Modifier.isPublic(context.method.getModifiers()))
            throw new IllegalAccessException("Method " + context.clazz.getName() + "#" + context.method.getName() + " is not public");

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
                            commandManager.getArgumentMapper().mapIndices(parameters, parsersResult, requirementsResult, List.of()),
                            context.subCommand.priority()
                        ),
                        REGISTERED_VARIANT_COMPARATOR
                );
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing method", e);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("There is an misused parameter for the following method " + context.clazz.getName() + "#" + context.method.getName() + "\nSub command value: " + context.subCommand.value(), e);
            }

            return;
        }

        CommandOption currentCommandOption = context.commandOption;
        List<CommandToken> tokens = commandManager.getCommandLexer().tokenize(context.subCommand.value());

        for (int i = 0; i < tokens.size(); i++) {
            CommandToken token = tokens.get(i);
            RequirementSet requirements = resolveRequirementsForBranch(i, methodRequirements);

            if (token instanceof KeywordToken keywordToken) {
                currentCommandOption = createKeywordOption(currentCommandOption, context.subCommand.ignoreCase(), keywordToken, requirements, requirementsResult);
                continue;
            }

            if (token instanceof ArgumentParserToken argumentParserToken) {
                currentCommandOption = createArgumentOption(currentCommandOption, argumentParserToken, methodArgumentTypeHandlerMap, requirements, parsersResult, requirementsResult, null);
                continue;
            }

            throw new UnknownTokenException(token);
        }


        currentCommandOption.getCommandInfo().fromCommandInfo(methodInfo);

        RegisteredVariant.Parameter[] parameters = RegisteredVariant.Parameter.of(context.commandNode, context.method.getParameters(), commandManager.argumentRequirementFactories);

        try {
            CollectionHelpers.addElementSorted(
                    currentCommandOption.getRegisteredCommandVariants(),
                    new RegisteredVariant(
                            publicLookup.unreflect(context.method),
                            parameters,
                            context.commandNode,
                            commandManager.getArgumentMapper().mapIndices(parameters, parsersResult, requirementsResult, List.of()),
                            context.subCommand.priority()
                    ),
                    REGISTERED_VARIANT_COMPARATOR
            );
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("There is an misused parameter for the following method " + context.clazz.getName() + "#" + context.method.getName() + "\nSub command value: " + context.subCommand.value(), e);
        }
    }

    //  TODO may need to split requirements so it will be faster and joined stuff
    private RequirementSet resolveRequirementsForBranch(int index, RequirementSet methodRequirements) {
        return index == 0
                ? methodRequirements
                : new RequirementSet();
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

    private CommandOption createKeywordOption(CommandOption currentCommandOption, boolean ignoreCase, KeywordToken keywordToken, RequirementSet requirements, List<Requirement> requirementsResult) {
        Map<String, CommandBranchProcessor> keywordMap = ignoreCase
                ? currentCommandOption.getKeywordIgnoreCaseMap()
                : currentCommandOption.getKeywordMap();

        String keyword = ignoreCase
                ? keywordToken.getKeyword().toLowerCase()
                : keywordToken.getKeyword();

        CommandBranchProcessor branchProcessor = keywordMap.computeIfAbsent(keyword, key -> new CommandBranchProcessor(commandManager));
        return createCommandOption(requirements, branchProcessor, requirementsResult);
    }

    private CommandOption createArgumentOption(CommandOption currentCommandOption, ArgumentParserToken argumentParserToken, Map<String, ArgumentParser<?>> parserMap, RequirementSet requirements, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult, ArgumentParser<?> fallbackParser) {
        RegisterArgumentParser<?> parser;
        try {
            parser = argumentParserToken.getParser(parserMap);
        } catch (Exception e) {
            parser = null;
        }

        if (parser == null && fallbackParser != null) {
            commandManager.getLogger().warning("Parser not found for: " + argumentParserToken.getParserName() + " using fallback parser: " + fallbackParser.getClass().getSimpleName());
            parser = new RegisterArgumentParser<>(
                    fallbackParser,
                    argumentParserToken.optionalNotMatch(),
                    argumentParserToken.isOptional(),
                    argumentParserToken.getParameterName()
                            .orElse(null)
            );
        }

        if (parser == null)
            throw new IllegalArgumentException("Parser not found & no fallback: " + argumentParserToken.getParserName());

        RegisterArgumentParser<?> finalParser = parser;
        Entry<RegisterArgumentParser<?>, CommandBranchProcessor> entryArgument = currentCommandOption.getArgumentTypeHandlerMap().stream()
                .filter(entry -> entry.key().equals(finalParser))
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
                    .filter(entry -> entry.key().equals(finalParser))
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
            requirements.addAll(RequirementFactory.createRequirementSet(commandNode, c.getAnnotations(), commandManager.requirementFactories, commandManager.getExternalRequirementFactories()));
        }

        return requirements;
    }

    private Set<Requirement> findAllRequirements(CommandNode commandNode, Method method) {
        Set<Requirement> requirements = new HashSet<>(RequirementFactory.createRequirementSet(commandNode, method.getAnnotations(), commandManager.requirementFactories, commandManager.getExternalRequirementFactories()));
        for (Method target : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            requirements.addAll(RequirementFactory.createRequirementSet(commandNode, target.getAnnotations(), commandManager.requirementFactories, commandManager.getExternalRequirementFactories()));
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

    public List<VirtualCommandNode> getVirtualNodes() {
        return virtualNodes;
    }

    public CommandBranchProcessor getCommandBranchProcessor() {
        return commandBranchProcessor;
    }

    public Iterable<HandleExceptionVariant> getHandleExceptionVariants() {
        return handleExceptionVariants;
    }
}
