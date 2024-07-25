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

import net.apartium.cocoabeans.utils.OptionalFloat;

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
                if (obj == null)
                    return OptionalDouble.empty();
                return OptionalDouble.of((Double) obj);
            },
            OptionalFloat.class, (obj) -> {
                if (obj == null)
                    return OptionalFloat.empty();
                return OptionalFloat.of((Float) obj);
            }
    );


    @Override
    public List<Object> map(CommandContext context, Sender sender, RegisteredCommandVariant registeredCommandVariant) {
        RegisteredCommandVariant.Parameter[] parameters = registeredCommandVariant.parameters();
        if (parameters.length == 0)
            return List.of(registeredCommandVariant.commandNode());

        List<Object> result = new ArrayList<>(parameters.length + 1);
        result.add(registeredCommandVariant.commandNode());

        Map<Class<?>, Integer> counterMap = new HashMap<>();
        Map<Class<?>, List<Object>> mapOfObjects = context.parsedArgs();

        for (int i = 1; i < parameters.length + 1; i++) {
            Class<?> type = parameters[i - 1].type();
            boolean optional = false;
            boolean optionalPrimitive = false;

            if (type == Optional.class) {
                optional = true;
                Type parameterizedType = parameters[i - 1].parameterizedType();
                Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
                type = (Class<?>) actualTypeArguments[0];
            } else if (OPTIONAL_TO_PRIMITIVE_MAP.containsKey(type)) {
                type = OPTIONAL_TO_PRIMITIVE_MAP.get(type);
                optionalPrimitive = true;
            }

            int index = counterMap.computeIfAbsent(type, (k) -> 0);

            if (Sender.class.isAssignableFrom(type)) {
                if (index != 0 && mapOfObjects.get(type) != null) {
                    result.add(mapOfObjects.get(type).get(index - 1));
                } else {
                    result.add(sender);
                }
            } else if (type.equals(CommandContext.class)) {
                if (index == 1) throw new RuntimeException("Shouldn't have two command context");
                result.add(context);
            } else {
                List<Object> objects = context.parsedArgs().get(type);

                if (objects == null || objects.isEmpty() || objects.size() <= index)
                    objects = context.parsedArgs().get(PRIMITIVE_TO_WRAPPER_MAP.getOrDefault(type, type));

                if (objects == null || objects.isEmpty() || objects.size() <= index)
                    throw new RuntimeException("No argument found for type " + type);

                Object obj = objects.get(index);
                if (optional) {
                    if (obj == null || (obj instanceof Optional<?> && ((Optional<?>) obj).isEmpty()))
                        obj = Optional.empty();
                    else
                        obj = Optional.of(obj);
                }

                if (optionalPrimitive) {
                    if (obj == null || (obj instanceof Optional<?> && ((Optional<?>) obj).isEmpty()))
                        obj = EMPTY_OPTIONAL.get(type);
                    else
                        obj = OF_OPTIONAL.get(PRIMITIVE_TO_OPTIMAL.get(type)).apply(obj);
                }

                result.add(obj);
            }

            counterMap.put(type, index + 1);
        }

        return result;
    }

}
