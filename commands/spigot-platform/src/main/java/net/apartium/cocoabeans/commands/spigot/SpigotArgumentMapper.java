/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot;

import net.apartium.cocoabeans.commands.CommandContext;
import net.apartium.cocoabeans.commands.ArgumentMapper;
import net.apartium.cocoabeans.commands.RegisteredCommandVariant;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.utils.OptionalFloat;
import org.bukkit.command.CommandSender;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import static net.apartium.cocoabeans.commands.SimpleArgumentMapper.PRIMITIVE_TO_WRAPPER_MAP;

public class SpigotArgumentMapper implements ArgumentMapper {

    @Override
    public List<Object> map(CommandContext context, Sender sender, RegisteredCommandVariant registeredCommandVariant) {
        // TODO merge with SimpleArgumentMapper
        RegisteredCommandVariant.Parameter[] parameters = registeredCommandVariant.parameters();
        if (parameters.length == 0)
            return List.of();

        List<Object> result = new ArrayList<>(parameters.length + 1);
        result.add(registeredCommandVariant.commandNode());

        Map<Class<?>, Integer> counterMap = new HashMap<>();
        Map<Class<?>, List<Object>> mapOfObjects = context.parsedArgs();

        for (int i = 1; i < parameters.length + 1; i++) {
            Class<?> type = parameters[i - 1].type();
            int index = counterMap.computeIfAbsent(type, (k) -> 0);

            if (handleResult(counterMap, result, get(type, counterMap, mapOfObjects, sender, Sender.class, -1)))
                continue;

            if (handleResult(counterMap, result, get(type, counterMap, mapOfObjects, sender.getSender(), CommandSender.class, -1)))
                continue;

            if (type.equals(CommandContext.class)) {
                if (index == 1) throw new RuntimeException("Shouldn't have two command context");
                result.add(context);
                counterMap.put(type, index + 1);
                continue;
            }

            List<Object> objects = context.parsedArgs().get(type);

            if (objects == null || objects.isEmpty() || objects.size() <= index)
                objects = context.parsedArgs().get(PRIMITIVE_TO_WRAPPER_MAP.getOrDefault(type, type));

            if (type == Optional.class) {
                type = (Class<?>) ((ParameterizedType) parameters[i - 1].parameterizedType()).getActualTypeArguments()[0];

                if (objects == null)
                    objects = context.parsedArgs().get(type);

                if (objects == null || objects.isEmpty() || objects.size() <= index)
                    throw new RuntimeException("No argument found for type " + type);

                Object obj = objects.get(index);

                if (obj == null || (obj instanceof Optional<?> && ((Optional<?>) obj).isEmpty()) ) {
                    result.add(Optional.empty());
                    counterMap.put(type, index + 1);
                    continue;
                }

                result.add(Optional.of(obj));
                counterMap.put(type, index + 1);
                continue;
            }

            if (
                    type == OptionalInt.class ||
                    type == OptionalLong.class ||
                    type == OptionalDouble.class ||
                    type == OptionalFloat.class
            ) {
                type = type == OptionalInt.class ? int.class : type == OptionalLong.class ? long.class : type == OptionalDouble.class ? double.class : float.class;

                if (objects == null)
                    objects = context.parsedArgs().get(type);

                if (objects == null || objects.isEmpty() || objects.size() <= index)
                    throw new RuntimeException("No argument found for type " + type);

                Object obj = objects.get(index);

                if (obj instanceof Optional<?> opt)
                    obj = opt.orElse(null);


                if (type == OptionalFloat.class) {
                    if (obj == null)
                        result.add(OptionalFloat.empty());
                    else
                        result.add(OptionalFloat.of((float) obj));

                    type = float.class;
                } else if (type == OptionalDouble.class) {
                    if (obj == null)
                        result.add(OptionalDouble.empty());
                    else
                        result.add(OptionalDouble.of((double) obj));
                    type = double.class;
                } else if (type == OptionalLong.class) {
                    if (obj == null)
                        result.add(OptionalLong.empty());
                    else
                        result.add(OptionalLong.of((long) obj));
                    type = long.class;
                } else {
                    if (obj == null)
                        result.add(OptionalInt.empty());
                    else
                        result.add(OptionalInt.of((int) obj));
                    type = int.class;
                }

                counterMap.put(type, index + 1);
                continue;
            }

            if (objects == null || objects.isEmpty() || objects.size() <= index) {
                for (Class<?> clazz : context.parsedArgs().keySet()) {
                    if (type.isAssignableFrom(clazz)) {
                        objects = context.parsedArgs().get(clazz);
                        break;
                    }
                }
            }

            if (objects == null || objects.isEmpty() || objects.size() <= index)
                throw new RuntimeException("No argument found for type " + type);

            Object obj = objects.get(index);

            result.add(obj);
            counterMap.put(type, index + 1);
        }

        return result;
    }

    public boolean handleResult(Map<Class<?>, Integer> counterMap, List<Object> result, Result resultObj) {
        if (resultObj == null)
            return false;

        result.add(resultObj.result);
        counterMap.put(resultObj.clazz, counterMap.computeIfAbsent(resultObj.clazz, (k) -> 0) + 1);
        return true;
    }

    public Result get(Class<?> type, Map<Class<?>, Integer> counterMap,  Map<Class<?>, List<Object>> mapOfObjects, Object obj, Class<?> resultType, int indexOffset) {
        int index = counterMap.computeIfAbsent(type, (k) -> 0);

        if (type.isAssignableFrom(obj.getClass()) && index == 0 && counterMap.getOrDefault(type, -1) == 0)
            return new Result(resultType, obj);

        indexOffset =  counterMap.getOrDefault(type, -1) == 0 ? -1 : 0;

        List<Object> objects = mapOfObjects.get(type);
        if (objects == null || objects.size() <= (index + indexOffset))
            return null;

        return new Result(type, objects.get(Math.max(0, index + indexOffset)));
    }

    public record Result(
            Class<?> clazz,
            Object result
    ) {

    }

}
