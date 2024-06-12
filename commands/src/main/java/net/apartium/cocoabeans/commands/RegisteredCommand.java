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
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.parsers.factory.ParserFactory;
import net.apartium.cocoabeans.commands.parsers.factory.WithParserFactory;
import net.apartium.cocoabeans.commands.requirements.*;
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


    RegisteredCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        commandBranchProcessor = new CommandBranchProcessor(commandManager);
    }

    public void addNode(CommandNode node) {
        Class<?> clazz = node.getClass();

        RequirementSet requirementSet = createRequirementSet(node, clazz.getAnnotations());

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
        Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>(commandManager.argumentTypeHandlerMap);

        // Add class parsers
        argumentTypeHandlerMap.putAll(serializeArgumentTypeHandler(node, clazz.getAnnotations()));

        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

        Method[] methods = clazz.getMethods();

        // Add source parsers
        for (Method method : methods) {
            SourceParser sourceParser = method.getAnnotation(SourceParser.class);
            if (sourceParser == null)
                continue;

            if (!method.getReturnType().equals(Map.class))
                throw new RuntimeException("Wrong return type: " + method.getReturnType());

            try {
                argumentTypeHandlerMap.put(sourceParser.keyword(), new SourceParserImpl<>(
                        node,
                        sourceParser.keyword(),
                        sourceParser.clazz(),
                        sourceParser.priority(),
                        publicLookup.unreflect(method),
                        sourceParser.resultMaxAgeInMills()
                ));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        CommandOption commandOption = createCommandOption(requirementSet, commandBranchProcessor);

        for (Method method : methods) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);
            for (SubCommand subCommand : subCommands) {
                parseSubCommand(method, subCommand, clazz, argumentTypeHandlerMap, requirementSet, publicLookup, node, commandOption);
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
        }

    }

    private void parseSubCommand(Method method, SubCommand subCommand, Class<?> clazz, Map<String, ArgumentParser<?>> argumentTypeHandlerMap, RequirementSet requirementSet, MethodHandles.Lookup publicLookup, CommandNode node, CommandOption commandOption) {
        if (subCommand == null)
            return;

        if (!Modifier.isPublic(method.getModifiers()))
            return;

        // TODO replace to warning?
        if (Modifier.isStatic(method.getModifiers()))
            throw new RuntimeException("Static method " + clazz.getName() + "#" + method.getName() + " is not supported");


        Map<String, ArgumentParser<?>> methodArgumentTypeHandlerMap = new HashMap<>(argumentTypeHandlerMap);
        methodArgumentTypeHandlerMap.putAll(serializeArgumentTypeHandler(node, method.getAnnotations()));

        RequirementSet methodRequirements = new RequirementSet(createRequirementSet(node, method.getAnnotations()), requirementSet);


        String[] split = subCommand.value().split("\\s+");
        if (split.length == 0 || split.length == 1 && split[0].isEmpty()) {
            CommandOption cmdOption = createCommandOption(methodRequirements, commandBranchProcessor);

            try {
                CollectionHelpers.addElementSorted(
                        cmdOption.getRegisteredCommandVariants(),
                        new RegisteredCommandVariant(
                            publicLookup.unreflect(method),
                            serializeParameters(node, method.getParameters()),
                            node,
                            subCommand.priority()
                        ),
                        REGISTERED_COMMAND_VARIANT_COMPARATOR
                );
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing method", e);
            }

            return;
        }

        CommandOption currentCommandOption = commandOption;
        for (int index = 0; index < split.length; index++) {
            String cmd = split[index];

            //  TODO may need to split requirements so it will be faster and joined stuff
            RequirementSet requirements = index == 0 ? methodRequirements : new RequirementSet();

            if (cmd.startsWith("<") && cmd.endsWith(">")) {
                // TODO may need to check that can be parser before doing all calculation
                ArgumentParser<?> typeParser = methodArgumentTypeHandlerMap.get(cmd.substring(1 + (cmd.startsWith("<??") ? 2 : cmd.startsWith("<?") ? 1 : 0), cmd.length() - 1));
                if (cmd.startsWith("<??"))
                    typeParser = new OptionalArgumentParser<>(typeParser, true);
                else if(cmd.startsWith("<?"))
                    typeParser = new OptionalArgumentParser<>(typeParser, false);

                if (typeParser == null)
                    throw new RuntimeException("Couldn't resolve " + clazz.getName() + "#" + method.getName() + " parser: " + cmd.substring(1, cmd.length() - 1));

                ArgumentParser<?> finalTypeParser = typeParser;
                CommandBranchProcessor commandBranchProcessor = currentCommandOption.getArgumentTypeHandlerMap().stream()
                        .filter(entry -> entry.key().equals(finalTypeParser))
                        .findAny()
                        .map(Entry::value)
                        .orElse(null);

                if (commandBranchProcessor == null) {
                    commandBranchProcessor = new CommandBranchProcessor(commandManager);
                    CollectionHelpers.addElementSorted(
                            currentCommandOption.getArgumentTypeHandlerMap(),
                            new Entry<>(
                                    typeParser,
                                    commandBranchProcessor
                            ),
                            (a, b) -> b.key().compareTo(a.key())
                    );
                }

                if (finalTypeParser instanceof OptionalArgumentParser) {
                    CommandBranchProcessor branchProcessor = currentCommandOption.getOptionalArgumentTypeHandlerMap().stream()
                            .filter(entry -> entry.key().equals(finalTypeParser))
                            .findAny()
                            .map(Entry::value)
                            .orElse(null);

                    if (branchProcessor == null) {
                        branchProcessor = commandBranchProcessor;
                        CollectionHelpers.addElementSorted(
                                currentCommandOption.getOptionalArgumentTypeHandlerMap(),
                                new Entry<>(
                                    (OptionalArgumentParser<?>) finalTypeParser,
                                    branchProcessor
                                ),
                                (a, b) -> b.key().compareTo(a.key())
                        );
                    }
                }

                currentCommandOption = createCommandOption(requirements, commandBranchProcessor);
                continue;

            }

            Map<String, CommandBranchProcessor> keywordMap = subCommand.ignoreCase()
                    ? currentCommandOption.getKeywordIgnoreCaseMap()
                    : currentCommandOption.getKeywordMap();

            CommandBranchProcessor commandBranchProcessor = keywordMap.computeIfAbsent(subCommand.ignoreCase() ? cmd.toLowerCase() : cmd, key -> new CommandBranchProcessor(commandManager));
            currentCommandOption = createCommandOption(requirements, commandBranchProcessor);
        }

        try {
            CollectionHelpers.addElementSorted(
                    currentCommandOption.getRegisteredCommandVariants(),
                    new RegisteredCommandVariant(
                        publicLookup.unreflect(method),
                        serializeParameters(node, method.getParameters()),
                        node,
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
                    return argumentRequirementType.value().getConstructor().newInstance();
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

    private CommandOption createCommandOption(RequirementSet requirements, CommandBranchProcessor commandBranchProcessor) {
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

        return cmdOption;
    }


    private RequirementSet createRequirementSet(CommandNode commandNode, Annotation[] annotations) {
        Set<Requirement> requirements = new HashSet<>();

        for (Annotation annotation : annotations) {
            CommandRequirementType commandRequirementType = annotation.annotationType().getAnnotation(CommandRequirementType.class);
            if (commandRequirementType == null)
                continue;

            RequirementFactory requirementFactory = commandManager.requirementFactories.computeIfAbsent(commandRequirementType.value(), (clazz) -> {
                try {
                    return commandRequirementType.value().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    return null;
                }
            });

            if (requirementFactory == null)
                continue;

            Requirement requirement = requirementFactory.getRequirement(commandNode, annotation);
            if (requirement == null)
                continue;
            requirements.add(requirement);
        }

        return new RequirementSet(requirements);
    }

    private Map<String, ArgumentParser<?>> serializeArgumentTypeHandler(CommandNode commandNode, Annotation[] annotations) {
        Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();


        if (annotations == null || annotations.length == 0) {
            return argumentTypeHandlerMap;
        }

        for (Annotation annotation : annotations) {;
            if (annotation instanceof WithParser withParser) {
                ArgumentParser<?> argumentTypeHandler;
                try {
                    Constructor<? extends ArgumentParser<?>>[] ctors = (Constructor<? extends ArgumentParser<?>>[]) withParser.value().getDeclaredConstructors();
                    argumentTypeHandler = newInstance((Constructor<ArgumentParser<?>>[]) ctors, withParser.priority());
                } catch (InstantiationException |  IllegalAccessException | InvocationTargetException e) {
                    continue;
                }

                argumentTypeHandlerMap.put(argumentTypeHandler.getKeyword(), argumentTypeHandler);
                continue;
            }

            WithParserFactory withParserFactory = annotation.annotationType().getAnnotation(WithParserFactory.class);
            if (withParserFactory == null)
                continue;

            ParserFactory parserFactory = commandManager.parserFactories.computeIfAbsent(withParserFactory.value(), (clazz) -> {
                try {
                    return withParserFactory.value().getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    return null;
                }
            });

            if (parserFactory == null)
                continue;


            ArgumentParser<?> argumentParser = parserFactory.getArgumentParser(commandNode, annotation);
            if (argumentParser == null)
                continue;

            argumentTypeHandlerMap.put(argumentParser.getKeyword(), argumentParser);
        }

        return argumentTypeHandlerMap;
    }

    private static <T> T newInstance(Constructor<T>[] ctors, int priority) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = null;

        if (ctors.length > 1) {
            for (Constructor<T> ctor : ctors) {
                if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0].equals(int.class))
                    constructor = ctor;
            }

        }

        if (constructor == null)
            constructor = ctors[0];

        Object[] params;
        if (constructor.getParameterCount() == 1) {
            params = new Object[] {priority};
        } else {
            if (priority > 0)
                SharedSecrets.LOGGER.log(System.Logger.Level.WARNING, "Registered parser {} with priority, but it doesn't support it", constructor.getDeclaringClass().getName());

            params = new Object[0];
        }
        return (T) constructor.newInstance(params);
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
