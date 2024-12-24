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

import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.utils.OptionalFloat;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class SimpleArgumentMapper implements ArgumentMapper {

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_MAP = Map.ofEntries(
            Map.entry(Byte.class, byte.class),
            Map.entry(Character.class, char.class),
            Map.entry(Short.class, short.class),
            Map.entry(Integer.class, int.class),
            Map.entry(Long.class, long.class),
            Map.entry(Float.class, float.class),
            Map.entry(Double.class, double.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(char.class, Character.class),
            Map.entry(short.class, Short.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(float.class, Float.class),
            Map.entry(double.class, Double.class)
    );

    private static final Map<Class<?>, Class<?>> OPTIONAL_TO_PRIMITIVE_MAP = Map.of(
            OptionalInt.class, int.class,
            OptionalLong.class, long.class,
            OptionalDouble.class, double.class,
            OptionalFloat.class, float.class
    );

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_OPTIMAL = Map.of(
            int.class, OptionalInt.class,
            long.class, OptionalLong.class,
            double.class, OptionalDouble.class,
            float.class, OptionalFloat.class,
            Integer.class, OptionalInt.class,
            Long.class, OptionalLong.class,
            Double.class, OptionalDouble.class,
            Float.class, OptionalFloat.class
    );

    private static final Map<Class<?>, Object> EMPTY_OPTIONAL = Map.of(
            int.class, OptionalInt.empty(),
            Integer.class, OptionalInt.empty(),
            long.class, OptionalLong.empty(),
            Long.class, OptionalLong.empty(),
            double.class, OptionalDouble.empty(),
            Double.class, OptionalDouble.empty(),
            float.class, OptionalFloat.empty(),
            Float.class, OptionalFloat.empty()
    );

    private static final Map<Class<?>, Function<Object, Object>> OF_OPTIONAL = Map.of(
            Optional.class, Optional::of,
            OptionalInt.class, (obj) -> {
                if (obj == null)
                    return OptionalInt.empty();
                return OptionalInt.of((Integer) obj);
            },
            OptionalLong.class, (obj) -> {
                if (obj == null)
                    return OptionalLong.empty();
                return OptionalLong.of((Long) obj);
            },
            OptionalDouble.class, (obj) -> {
                if (obj == null || Double.isNaN((Double) obj))
                    return OptionalDouble.empty();
                return OptionalDouble.of((Double) obj);
            },
            OptionalFloat.class, (obj) -> {
                if (obj == null || Float.isNaN((Float) obj))
                    return OptionalFloat.empty();
                return OptionalFloat.of((Float) obj);
            }
    );

    @Override
    public List<ArgumentIndex<?>> mapIndices(RegisteredVariant.Parameter[] parameters, List<RegisterArgumentParser<?>> argumentParsers, List<Requirement> requirements, List<Class<?>> additionalTypes) {
        if (parameters.length == 0)
            return List.of();

        List<ArgumentIndex<?>> result = new ArrayList<>(parameters.length);

        Map<Class<?>, Integer> counterMap = new HashMap<>();
        ResultMap resultMap = createParsedArgs(argumentParsers, requirements, additionalTypes, getParametersNames(parameters));

        for (RegisteredVariant.Parameter parameter : parameters) {
            Class<?> type = parameter.type();

            boolean optional = false;
            boolean optionalPrimitive = false;

            if (type == Optional.class) {
                optional = true;
                type = getGenericType(parameter.parameterizedType());
            } else if (OPTIONAL_TO_PRIMITIVE_MAP.containsKey(type)) {
                type = OPTIONAL_TO_PRIMITIVE_MAP.get(type);
                optionalPrimitive = true;
            }

            int index = counterMap.computeIfAbsent(type, k -> 0);

            ArgumentIndex<?> argumentIndex;
            try {
                argumentIndex = resolveArgumentIndex(type, parameter.parameterName(), counterMap, resultMap, index);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("There is no argument for parameter " + parameter.parameterName() + " at index " + index + ".", e);
            }

            if (optional)
                argumentIndex = wrapInOptional(argumentIndex);

            if (optionalPrimitive)
                argumentIndex = wrapInOptionalPrimitive(argumentIndex, type);

            result.add(argumentIndex);
            counterMap.put(type, counterMap.get(type) + 1);
        }

        return result;
    }

    private Map<String, Class<?>> getParametersNames(RegisteredVariant.Parameter[] parameters) {
        Map<String, Class<?>> result = new HashMap<>();

        for (RegisteredVariant.Parameter parameter : parameters) {
            if (parameter.parameterName() == null)
                continue;

            if (result.containsKey(parameter.parameterName()))
                throw new IllegalArgumentException("Duplicate parameter name " + parameter.parameterName());

            result.put(parameter.parameterName(), parameter.type());
        }

        return result;
    }

    private Class<?> getGenericType(Type parameterizedType) {
        return (Class<?>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
    }

    private ArgumentIndex<Optional<?>> wrapInOptional(ArgumentIndex<?> argumentIndex) {
        return context -> {
            Object obj = argumentIndex.get(context);
            if (obj == null)
                return Optional.empty();

            if (obj instanceof Optional<?>)
                return (Optional<?>) obj;

            return Optional.of(obj);
        };
    }

    private ArgumentIndex<?> wrapInOptionalPrimitive(ArgumentIndex<?> argumentIndex, Class<?> type) {
        return context -> {
            Object obj = argumentIndex.get(context);

            if (obj instanceof Optional<?> opt)
                obj = opt.orElse(null);

            if (obj == null)
                return EMPTY_OPTIONAL.get(type);

            return OF_OPTIONAL.get(PRIMITIVE_TO_OPTIMAL.get(type)).apply(obj);
        };
    }

    private ArgumentIndex<?> resolveArgumentIndex(Class<?> type, String name, Map<Class<?>, Integer> counterMap, ResultMap resultMap, int index) {
        if (name != null && resultMap.mapOfArgumentsByParameterName.containsKey(name)) {
            counterMap.put(type, counterMap.get(type) - 1);
            return resultMap.mapOfArgumentsByParameterName.get(name);
        }

        ArgumentIndex<?> argumentIndex = resolveBuiltInArgumentIndex(type, counterMap, resultMap.mapOfArgumentsByType, index);
        if (argumentIndex != null)
            return argumentIndex;

        List<ArgumentIndex<?>> arguments = resultMap.mapOfArgumentsByType.get(type);

        if (isInvalidArguments(arguments, index))
            arguments = findArgumentsByWrapperType(type, resultMap.mapOfArgumentsByType);

        if (isInvalidArguments(arguments, index))
            arguments = findArgumentsByAssignableType(type, resultMap.mapOfArgumentsByType);


        if (isInvalidArguments(arguments, index))
            throw new NoSuchElementException("No argument found for type " + type + " at index " + index);


        return arguments.get(index);
    }

    private boolean isInvalidArguments(List<ArgumentIndex<?>> arguments, int index) {
        return arguments == null || arguments.size() <= index;
    }

    private List<ArgumentIndex<?>> findArgumentsByWrapperType(Class<?> type, Map<Class<?>, List<ArgumentIndex<?>>> mapOfArguments) {
        return mapOfArguments.get(PRIMITIVE_TO_WRAPPER_MAP.getOrDefault(type, type));
    }

    private List<ArgumentIndex<?>> findArgumentsByAssignableType(Class<?> type, Map<Class<?>, List<ArgumentIndex<?>>> mapOfArguments) {
        return mapOfArguments.entrySet().stream()
                .filter(entry -> type.isAssignableFrom(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    protected ArgumentIndex<?> resolveBuiltInArgumentIndex(Class<?> type, Map<Class<?>, Integer> counterMap, Map<Class<?>, List<ArgumentIndex<?>>> mapOfArguments, int index) {
        if (Sender.class.isAssignableFrom(type))
            return getSenderIndex(mapOfArguments, index);

        if (CommandContext.class.equals(type)) {
            if (index != 0)
                throw new IllegalArgumentException("Can't use index " + index + " for CommandContext");

            return context -> context.parsedArgs().get(CommandContext.class).get(0);
        }

        return null;
    }

    private ArgumentIndex<?> getSenderIndex(Map<Class<?>, List<ArgumentIndex<?>>> mapOfArguments, int index) {
        if (index == 0)
            return ArgumentContext::sender;

        return mapOfArguments.get(Sender.class).get(index - 1);
    }

    private ResultMap createParsedArgs(List<RegisterArgumentParser<?>> argumentParsers, List<Requirement> requirements, List<Class<?>> additionalTypes, Map<String, Class<?>> lookupParametersNames) {
        Map<Class<?>, List<ArgumentIndex<?>>> resultMap = new HashMap<>();
        Map<String, ArgumentIndex<?>> resultParameterName = new HashMap<>();

        Map<Class<?>, Integer> counterMap = new HashMap<>();

        checkDuplicateArgumentParsersName(argumentParsers);

        for (RegisterArgumentParser<?> argumentParser : argumentParsers) {
            serializesArgumentIndex(argumentParser.getArgumentType(), argumentParser.getParameterName().orElse(null), lookupParametersNames, counterMap, resultMap, resultParameterName);
        }

        for (Requirement requirement : requirements) {
            for (Class<?> type : requirement.getTypes()) {
                serializesArgumentIndex(type, null, lookupParametersNames, counterMap, resultMap, resultParameterName);
            }
        }

        for (Class<?> type : additionalTypes) {
            serializesArgumentIndex(type, null, lookupParametersNames, counterMap, resultMap, resultParameterName);
        }

        return new ResultMap(resultMap, resultParameterName);
    }

    private void checkDuplicateArgumentParsersName(List<RegisterArgumentParser<?>> argumentParsers) {
        Set<String> visitedNames = new HashSet<>();

        for (RegisterArgumentParser<?> argumentParser : argumentParsers) {
            String name = argumentParser.getParameterName().orElse(null);
            if (name == null)
                continue;

            if (!visitedNames.add(name))
                throw new IllegalArgumentException("Duplicate parameter name " + name);
        }
    }

    private void serializesArgumentIndex(Class<?> type, String parameterName, Map<String, Class<?>> lookupParametersNames, Map<Class<?>, Integer> counterMap, Map<Class<?>, List<ArgumentIndex<?>>> resultMap, Map<String, ArgumentIndex<?>> resultParameterName) {
        int countIndex = counterMap.getOrDefault(type, 0);
        counterMap.put(type, countIndex + 1);

        if (parameterName != null && lookupParametersNames.containsKey(parameterName)) {
            if (!lookupParametersNames.get(parameterName).isAssignableFrom(type))
                throw new IllegalArgumentException("Parameter name " + parameterName + " is not assignable from type " + type);

            resultParameterName.put(parameterName, context -> context.parsedArgs().get(type).get(countIndex));
            return;
        }

        resultMap.computeIfAbsent(type, k -> new ArrayList<>())
                .add(context -> context.parsedArgs().get(type).get(countIndex));
    }


    /**
     * Result of mapping arguments to index
     * @param mapOfArgumentsByType map of arguments by type
     * @param mapOfArgumentsByParameterName map of arguments by parameter name
     */
    @ApiStatus.AvailableSince("0.0.37")
    public record ResultMap(
            Map<Class<?>, List<ArgumentIndex<?>>> mapOfArgumentsByType,
            Map<String, ArgumentIndex<?>> mapOfArgumentsByParameterName
    ) { }

}
