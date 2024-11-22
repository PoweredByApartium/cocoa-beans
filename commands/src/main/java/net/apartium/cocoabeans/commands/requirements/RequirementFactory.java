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

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public interface RequirementFactory {

    /**
     * Get the class of the requirement factory
     * @param annotation annotation that has a CommandRequirementType
     * @return the class of the requirement factory
     */
    @ApiStatus.AvailableSince("0.0.37")
    static Class<? extends RequirementFactory> getRequirementFactoryClass(Annotation annotation) {
        if (annotation == null)
            return null;

        CommandRequirementType commandRequirementType = annotation.annotationType().getAnnotation(CommandRequirementType.class);
        return commandRequirementType == null ? null : commandRequirementType.value();
    }

    /**
     * Create a requirement factory from an annotation
     * @param annotation annotation that has a CommandRequirementType
     * @return the created requirement factory
     */
    @ApiStatus.AvailableSince("0.0.37")
    static RequirementFactory createFromAnnotation(Annotation annotation) {
        if (annotation == null)
            return null;

        Class<? extends RequirementFactory> requirementFactoryClass = getRequirementFactoryClass(annotation);

        try {
            return requirementFactoryClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Failed to instantiate requirement factory: " + requirementFactoryClass, e);
        }
    }

    /**
     * Create a requirement from the given object
     * @param commandNode command node
     * @param obj object
     * @return requirement or null
     */
    @Nullable
    Requirement getRequirement(CommandNode commandNode, Object obj);

}

