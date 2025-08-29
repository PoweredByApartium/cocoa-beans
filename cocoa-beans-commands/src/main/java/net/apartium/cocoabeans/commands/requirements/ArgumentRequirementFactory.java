/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.GenericNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArgumentRequirementFactory {

    /**
     * Create an argument requirement from the given annotations
     * @param node node
     * @param annotations annotations
     * @param argumentRequirementFactories argument requirement factories
     * @return argument requirements
     */
    @ApiStatus.AvailableSince("0.0.37")
    static ArgumentRequirement[] createArgumentRequirements(GenericNode node, Annotation[] annotations, Map<Class<? extends ArgumentRequirementFactory>, ArgumentRequirementFactory> argumentRequirementFactories) {
        if (annotations == null)
            return new ArgumentRequirement[0];

        List<ArgumentRequirement> result = new ArrayList<>();

        for (Annotation annotation : annotations) {
            Class<? extends ArgumentRequirementFactory> argumentRequirementType = ArgumentRequirementFactory.getArgumentRequirementFactoryClass(annotation);

            if (argumentRequirementType == null)
                continue;

            ArgumentRequirement argumentRequirement = Optional.ofNullable(argumentRequirementFactories.computeIfAbsent(
                            argumentRequirementType,
                            clazz -> ArgumentRequirementFactory.createFromAnnotation(annotation)
                    ))
                    .map(factory -> factory.getArgumentRequirement(node, annotation))
                    .orElse(null);

            if (argumentRequirement == null)
                continue;

            result.add(argumentRequirement);
        }

        return result.toArray(new ArgumentRequirement[0]);

    }

    /**
     * Get the class of the argument requirement factory
     * @param annotation annotation that has a ArgumentRequirementType
     * @return the class of the argument requirement factory
     */
    @ApiStatus.AvailableSince("0.0.37")
    static Class<? extends ArgumentRequirementFactory> getArgumentRequirementFactoryClass(Annotation annotation) {
        if (annotation == null)
            return null;

        ArgumentRequirementType argumentRequirementType = annotation.annotationType().getAnnotation(ArgumentRequirementType.class);
        return argumentRequirementType == null ? null : argumentRequirementType.value();
    }

    /**
     * Create an argument requirement factory
     * @param annotation annotation
     * @return argument requirement factory
     */
    @ApiStatus.AvailableSince("0.0.37")
    static ArgumentRequirementFactory createFromAnnotation(Annotation annotation) {
        if (annotation == null)
            return null;

        Class<? extends ArgumentRequirementFactory> clazz = getArgumentRequirementFactoryClass(annotation);

        if (clazz == null)
            return null;

        try {
            Constructor<? extends ArgumentRequirementFactory> constructor = clazz.getConstructor();
            if (constructor.getParameterCount() == 0)
                return constructor.newInstance();

            return null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate argument requirement factory: " + clazz, e);
        }
    }

    /**
     * Create an argument requirement from the given object
     * @param commandNode command node
     * @param obj object
     * @return argument requirement or null
     */
    @Nullable
    ArgumentRequirement getArgumentRequirement(GenericNode commandNode, Object obj);

}
