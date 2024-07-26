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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Represents a requirement to run a command or a sub command, eg a permission, sender type (player / console etc)
 * @see RequirementFactory
 */
public interface Requirement {

    static Requirement createRequirement(Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories, CommandNode commandNode, Annotation annotation) {
        CommandRequirementType commandRequirementType = annotation.annotationType().getAnnotation(CommandRequirementType.class);
        if (commandRequirementType == null)
            return null;

        RequirementFactory requirementFactory = requirementFactories.computeIfAbsent(commandRequirementType.value(), (clazz) -> {
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



    /**
     * Check if given context meets this requirement.
     * @param context context to evaluate
     * @return if sender meets requirement or else given an error
     */
    RequirementResult meetsRequirement(RequirementEvaluationContext context);


}
