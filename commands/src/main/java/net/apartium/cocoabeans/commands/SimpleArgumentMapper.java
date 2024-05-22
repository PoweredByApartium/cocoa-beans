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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleArgumentMapper implements ArgumentMapper {
    @Override
    public List<Object> map(CommandContext context, Sender sender, RegisteredCommandVariant registeredCommandVariant) {
        RegisteredCommandVariant.Parameter[] parameters = registeredCommandVariant.parameters();
        if (parameters.length == 0)
            return List.of();

        List<Object> result = new ArrayList<>(parameters.length + 1);
        result.add(registeredCommandVariant.commandNode());

        Map<Class<?>, Integer> counterMap = new HashMap<>();
        Map<Class<?>, List<Object>> mapOfObjects = context.parsedArgs();

        for (int i = 1; i < parameters.length + 1; i++) {
            Class<?> type = parameters[i -1].type();
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
                    throw new RuntimeException("No argument found for type " + type);
                result.add(objects.get(index));
            }

            counterMap.put(type, index + 1);
        }

        return result;
    }
}
