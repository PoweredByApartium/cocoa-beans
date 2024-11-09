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
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.exception.HandleExceptionVariant;
import net.apartium.cocoabeans.commands.lexer.ArgumentParserToken;
import net.apartium.cocoabeans.commands.lexer.CommandLexer;
import net.apartium.cocoabeans.commands.lexer.CommandToken;
import net.apartium.cocoabeans.commands.lexer.KeywordToken;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.reflect.ClassUtils;
import net.apartium.cocoabeans.reflect.MethodUtils;
import net.apartium.cocoabeans.structs.Entry;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;

/*package-private*/ class RegisteredCommand {

    private static final Comparator<HandleExceptionVariant> HANDLE_EXCEPTION_VARIANT_COMPARATOR = (a, b) -> Integer.compare(b.priority(), a.priority());

    private static final Comparator<RegisteredCommandVariant> REGISTERED_COMMAND_VARIANT_COMPARATOR = (a, b) -> Integer.compare(b.priority(), a.priority());

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
                        createRequirementSet(node, fallbackHandle.getAnnotations())
                ))
        );
        Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

        // Add class parsers & class parsers
        for (Class<?> c : ClassUtils.getSuperClassAndInterfaces(clazz)) {
            for (var entry : serializeArgumentTypeHandler(node, c.getAnnotations(), c, true).entrySet()) {
                argumentTypeHandlerMap.putIfAbsent(entry.getKey(), entry.getValue());
            }

            for (Method method : clazz.getMethods()) {
                try {
                    addParsers(
                            node,
                            argumentTypeHandlerMap,
                            method,
                            c.getMethod(method.getName(), method.getParameterTypes()),
                            true
                    );

                } catch (NoSuchMethodException ignored) {
                }
            }

        }

        for (var entry : commandManager.argumentTypeHandlerMap.entrySet()) {
            argumentTypeHandlerMap.putIfAbsent(entry.getKey(), entry.getValue());
        }


        List<Requirement> classRequirementsResult = new ArrayList<>();
        CommandOption commandOption = createCommandOption(requirementSet, commandBranchProcessor, classRequirementsResult);

        for (Method method : clazz.getMethods()) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

            for (SubCommand subCommand : subCommands) {
                parseSubCommand(method, subCommand, clazz, argumentTypeHandlerMap, requirementSet, publicLookup, node, commandOption, new ArrayList<>(), new ArrayList<>(classRequirementsResult));
            }

            ExceptionHandle exceptionHandle = method.getAnnotation(ExceptionHandle.class);
            if (exceptionHandle != null) {
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
                    throw new RuntimeException(e);
                }
            }


            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
                handleSubCommand(node, clazz, requirementSet, argumentTypeHandlerMap, publicLookup, commandOption, method, targetMethod, classRequirementsResult);
            }

        }

    }

    private void handleSubCommand(CommandNode node, Class<?> clazz, RequirementSet requirementSet, Map<String, ArgumentParser<?>> argumentTypeHandlerMap, MethodHandles.Lookup publicLookup, CommandOption commandOption, Method method, Method targetMethod, List<Requirement> classRequirementsResult) {
        ExceptionHandle exceptionHandle;
        if (targetMethod == null)
            return;



        SubCommand[] superSubCommands = targetMethod.getAnnotationsByType(SubCommand.class);

        for (SubCommand subCommand : superSubCommands) {
            parseSubCommand(method, subCommand, clazz, argumentTypeHandlerMap, requirementSet, publicLookup, node, commandOption, new ArrayList<>(), new ArrayList<>(classRequirementsResult));
        }

        exceptionHandle = targetMethod.getAnnotation(ExceptionHandle.class);
        if (exceptionHandle != null) {
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
                throw new RuntimeException(e);
            }
        }
    }


    private void addParsers(CommandNode node, Map<String, ArgumentParser<?>> argumentTypeHandlerMap, Method method, Method targetMethod, boolean onlyClassParser) {
        Annotation[] annotations = targetMethod.getAnnotations();

        for (Annotation annotation : annotations) {
            handleParserFactories(node, method, argumentTypeHandlerMap, annotation, onlyClassParser);
        }

    }

    private void parseSubCommand(Method method, SubCommand subCommand, Class<?> clazz, Map<String, ArgumentParser<?>> argumentTypeHandlerMap, RequirementSet requirementSet, MethodHandles.Lookup publicLookup, CommandNode node, CommandOption commandOption, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult) {
        if (subCommand == null)
            return;

        if (!Modifier.isPublic(method.getModifiers()))
            return;

        // TODO replace to warning?
        if (Modifier.isStatic(method.getModifiers()))
            throw new RuntimeException("Static method " + clazz.getName() + "#" + method.getName() + " is not supported");


        Map<String, ArgumentParser<?>> methodArgumentTypeHandlerMap = new HashMap<>(serializeArgumentTypeHandler(node, method.getAnnotations(), method, false));

        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            Map<String, ArgumentParser<?>> withParserMap = serializeArgumentTypeHandler(node, targetMethod.getAnnotations(), targetMethod, false);
            for (var entry : withParserMap.entrySet()) {
                if (methodArgumentTypeHandlerMap.containsKey(entry.getKey()))
                    continue;

                methodArgumentTypeHandlerMap.put(entry.getKey(), entry.getValue());
            }
        }

        for (var entry : argumentTypeHandlerMap.entrySet()) {
            if (methodArgumentTypeHandlerMap.containsKey(entry.getKey()))
                continue;

            methodArgumentTypeHandlerMap.put(entry.getKey(), entry.getValue());
        }

        CommandInfo methodInfo = new CommandInfo();

        methodInfo.fromAnnotations(method.getAnnotations(), true);
        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            methodInfo.fromAnnotations(targetMethod.getAnnotations(), false);
        }

        RequirementSet methodRequirements = new RequirementSet(
                findAllRequirements(node, method),
                requirementSet
        );


        String[] split = subCommand.value().split("\\s+");
        if (split.length == 0 || split.length == 1 && split[0].isEmpty()) {
            CommandOption cmdOption = createCommandOption(methodRequirements, commandBranchProcessor, requirementsResult);

            cmdOption.getCommandInfo().fromCommandInfo(methodInfo);
            RegisteredCommandVariant.Parameter[] parameters = serializeParameters(node, method.getParameters());

            try {
                CollectionHelpers.addElementSorted(
                        cmdOption.getRegisteredCommandVariants(),
                        new RegisteredCommandVariant(
                            publicLookup.unreflect(method),
                            parameters,
                            node,
                            commandManager.getArgumentMapper().mapIndices(parameters, parsersResult, requirementsResult),
                            subCommand.priority()
                        ),
                        REGISTERED_COMMAND_VARIANT_COMPARATOR
                );
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing method", e);
            }

            return;
        }

        // TODO split it to methods
        CommandOption currentCommandOption = commandOption;
        // TODO temp
        List<CommandToken> tokens = commandManager.getCommandLexer().tokenization(subCommand.value());

        for (int i = 0; i < tokens.size(); i++) {
            CommandToken token = tokens.get(i);

            //  TODO may need to split requirements so it will be faster and joined stuff
            RequirementSet requirements = i == 0 ? methodRequirements : new RequirementSet();

            // TODO move to 2 methods
            if (token instanceof KeywordToken keywordToken) {
                Map<String, CommandBranchProcessor> keywordMap = subCommand.ignoreCase()
                        ? currentCommandOption.getKeywordIgnoreCaseMap()
                        : currentCommandOption.getKeywordMap();

                String keyword = subCommand.ignoreCase()
                        ? keywordToken.getKeyword().toLowerCase()
                        : keywordToken.getKeyword();

                CommandBranchProcessor commandBranchProcessor = keywordMap.computeIfAbsent(keyword, key -> new CommandBranchProcessor(commandManager));
                currentCommandOption = createCommandOption(requirements, commandBranchProcessor, requirementsResult);
                continue;
            }

            if (token instanceof ArgumentParserToken argumentParserToken) {
                RegisterArgumentParser<?> parser = argumentParserToken.getParser(argumentTypeHandlerMap);

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

                currentCommandOption = createCommandOption(requirements, commandBranchProcessor, requirementsResult);
                continue;
            }

            throw new RuntimeException("Unknown token while parsing " + token);
        }


        currentCommandOption.getCommandInfo().fromCommandInfo(methodInfo);

        RegisteredCommandVariant.Parameter[] parameters = serializeParameters(node, method.getParameters());

        try {
            CollectionHelpers.addElementSorted(
                    currentCommandOption.getRegisteredCommandVariants(),
                    new RegisteredCommandVariant(
                        publicLookup.unreflect(method),
                        parameters,
                        node,
                        commandManager.getArgumentMapper().mapIndices(parameters, parsersResult, requirementsResult),
                        subCommand.priority()),
                    REGISTERED_COMMAND_VARIANT_COMPARATOR
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing method", e);
        }
    }

    private RegisteredCommandVariant.Parameter[] serializeParameters(CommandNode commandNode, Parameter[] parameters) {
        RegisteredCommandVariant.Parameter[] result = new RegisteredCommandVariant.Parameter[parameters.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new RegisteredCommandVariant.Parameter(
                    parameters[i].getType(),
                    parameters[i].getParameterizedType(),
                    serializeArgumentRequirement(commandNode, parameters[i].getAnnotations())
            );
        }
        return result;
    }

    private ArgumentRequirement[] serializeArgumentRequirement(CommandNode commandNode, Annotation[] annotations) {
        List<ArgumentRequirement> result = new ArrayList<>();

        for (Annotation annotation : annotations) {
            ArgumentRequirementType argumentRequirementType = annotation.annotationType().getAnnotation(ArgumentRequirementType.class);
            if (argumentRequirementType == null)
                continue;

            ArgumentRequirementFactory factory = commandManager.argumentRequirementFactories.computeIfAbsent(argumentRequirementType.value(), (clazz) -> {
                try {
                    Constructor<? extends ArgumentRequirementFactory> constructor = argumentRequirementType.value().getConstructor();
                    if (constructor.getParameterCount() == 0)
                        return constructor.newInstance();

                    if (constructor.getParameters().length == 1 && constructor.getParameterTypes()[0].equals(CommandManager.class))
                        return constructor.newInstance(commandManager);

                    return null;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    return null;
                }
            });

            if (factory == null)
                continue;

            ArgumentRequirement argumentRequirement = factory.getArgumentRequirement(commandNode, annotation);
            if (argumentRequirement == null)
                continue;

            result.add(argumentRequirement);
        }

        return result.toArray(new ArgumentRequirement[0]);
    }

    private CommandOption createCommandOption(RequirementSet requirements, CommandBranchProcessor commandBranchProcessor, List<Requirement> requirementsResult) {
        CommandOption cmdOption = commandBranchProcessor.objectMap.stream()
                .filter(entry -> entry.key().equals(requirements))
                .findAny()
                .map(Entry::value)
                .orElse(null);

        if (cmdOption == null) {
            cmdOption = new CommandOption(commandManager);
            commandBranchProcessor.objectMap.add(new Entry<>(
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
            requirements.addAll(createRequirementSet(commandNode, c.getAnnotations()));
        }

        return requirements;
    }

    private Set<Requirement> findAllRequirements(CommandNode commandNode, Method method) {
        Set<Requirement> requirements = new HashSet<>(createRequirementSet(commandNode, method.getAnnotations()));
        for (Method target : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            requirements.addAll(createRequirementSet(commandNode, target.getAnnotations()));
        }

        return requirements;
    }

    private Requirement getRequirement(CommandNode commandNode, Annotation annotation) {
        CommandRequirementType commandRequirementType = annotation.annotationType().getAnnotation(CommandRequirementType.class);
        if (commandRequirementType == null)
            return null;

        RequirementFactory requirementFactory = commandManager.requirementFactories.computeIfAbsent(commandRequirementType.value(), (clazz) -> {
            try {
                return commandRequirementType.value().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        });

        if (requirementFactory == null)
            return null;

        return requirementFactory.getRequirement(commandNode, annotation);
    }


    private Set<Requirement> createRequirementSet(CommandNode commandNode, Annotation[] annotations) {
        if (annotations == null || annotations.length == 0)
            return Collections.emptySet();

        Set<Requirement> requirements = new HashSet<>();

        for (Annotation annotation : annotations) {
            Requirement requirement = getRequirement(commandNode, annotation);
            if (requirement == null)
                continue;

            requirements.add(requirement);
        }

        return requirements;
    }

    private Map<String, ArgumentParser<?>> serializeArgumentTypeHandler(CommandNode commandNode, Annotation[] annotations, GenericDeclaration obj, boolean onlyClassParser) {
        Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();


        if (annotations == null) {
            return argumentTypeHandlerMap;
        }

        for (Annotation annotation : annotations) {
            handleParserFactories(commandNode, obj, argumentTypeHandlerMap, annotation, onlyClassParser);
        }

        return argumentTypeHandlerMap;
    }

    private void handleParserFactories(CommandNode commandNode, GenericDeclaration obj, Map<String, ArgumentParser<?>> argumentTypeHandlerMap, Annotation annotation, boolean onlyClassParser) {
        CommandParserFactory commandParserFactory = annotation.annotationType().getAnnotation(CommandParserFactory.class);
        if (commandParserFactory == null)
            return;

        if (!commandParserFactory.scope().isClass() && onlyClassParser)
            return;

        ParserFactory parserFactory = commandManager.parserFactories.computeIfAbsent(commandParserFactory.value(), (clazz) -> {
            try {
                return commandParserFactory.value().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        });

        if (parserFactory == null)
            return;

        Collection<ParserFactory.ParserResult> parserResults = parserFactory.getArgumentParser(commandNode, annotation, obj);
        if (parserResults.isEmpty())
            return;

        for (ParserFactory.ParserResult parseResult : parserResults) {
            if (!parseResult.scope().isClass() && onlyClassParser)
                continue;

            argumentTypeHandlerMap.put(parseResult.parser().getKeyword(), parseResult.parser());
        }
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
