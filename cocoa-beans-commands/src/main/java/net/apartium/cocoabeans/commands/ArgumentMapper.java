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

import java.util.List;

/**
 * Maps the command arguments into the parameters required by the sub command
 * @see SimpleArgumentMapper
 */
public interface ArgumentMapper {

    /**
     * Maps the command arguments into the argument indexes required by the sub command to be call with the right arguments
     * @param parameters parameters
     * @param argumentParsers argumentParsers (should be in order)
     * @param requirements requirements
     * @param additionalTypes additional types are use for providing additional arguments to the command they are inserted at the end
     * @return list of argument indexes
     */
    List<ArgumentIndex<?>> mapIndices(RegisteredVariant.Parameter[] parameters, List<RegisterArgumentParser<?>> argumentParsers, List<Requirement> requirements, List<Class<?>> additionalTypes);

}
