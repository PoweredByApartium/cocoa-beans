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

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

/**
 * Represents a requirement to run a command or a sub command, eg a permission, sender type (player / console etc)
 * @see RequirementFactory
 */
public interface Requirement {


    /**
     * Check if given context meets this requirement.
     * @param context context to evaluate
     * @return if sender meets requirement or else given an error
     */
    RequirementResult meetsRequirement(RequirementEvaluationContext context);

    /**
     * Get list of classes that will be return if requirement is met
     * Note: By default returns an empty list
     * @return list of classes that will be return if requirement is met
     */
    @ApiStatus.AvailableSince("0.0.36")
    default List<Class<?>> getTypes() {
        return Collections.emptyList();
    }


}
