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

import net.apartium.cocoabeans.commands.requirements.ArgumentRequirement;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record RegisteredCommandVariant(
        MethodHandle method,
        Parameter[] parameters,
        CommandNode commandNode,
        List<ArgumentIndex<?>> argumentIndexList,
        int priority
) {

    public record Parameter(
            Class<?> type,
            Type parameterizedType,
            ArgumentRequirement[] argumentRequirements,
            String parameterName
    ) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Parameter parameter)) return false;
            return Objects.equals(type, parameter.type)
                    && Objects.equals(parameterName, parameter.parameterName)
                    && Objects.equals(parameterizedType, parameter.parameterizedType)
                    && Objects.deepEquals(argumentRequirements, parameter.argumentRequirements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    type,
                    parameterizedType,
                    Arrays.hashCode(argumentRequirements),
                    parameterName
            );
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "type=" + type +
                    ", parameterizedType=" + parameterizedType +
                    ", argumentRequirements=" + Arrays.toString(argumentRequirements) +
                    ", parameterName='" + parameterName + '\'' +
                    '}';
        }
    }

}
