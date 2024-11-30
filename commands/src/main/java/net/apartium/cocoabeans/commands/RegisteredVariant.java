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
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirementFactory;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Registered variant for a command
 * @param method target method to run
 * @param parameters parameters
 * @param node node instance
 * @param argumentIndexList list of argument indexes that will be passed to the method
 * @param priority the priority of the variant
 */
public record RegisteredVariant(
        MethodHandle method,
        Parameter[] parameters,
        GenericNode node,
        List<ArgumentIndex<?>> argumentIndexList,
        int priority
) {

    /**
     * Comparator for registered variants
     */
    public static final Comparator<RegisteredVariant> REGISTERED_VARIANT_COMPARATOR = (a, b) -> Integer.compare(b.priority(), a.priority());

    /**
     * Parameter represent a parameter of the command after has been parsed
     * @param type The type of parameter
     * @param parameterizedType parameterized type
     * @param argumentRequirements argument requirements
     * @param parameterName parameter name
     */
    public record Parameter(
            Class<?> type,
            Type parameterizedType,
            ArgumentRequirement[] argumentRequirements,
            String parameterName
    ) {

        /**
         * create parameters from the given parameters
         * @param node instance of the node
         * @param parameters parameters
         * @param argumentRequirementFactories argument requirement factories for caching
         * @return parameters parsed
         */
        @ApiStatus.AvailableSince("0.0.37")
        public static Parameter[] of(GenericNode node, java.lang.reflect.Parameter[] parameters, Map<Class<? extends ArgumentRequirementFactory>, ArgumentRequirementFactory> argumentRequirementFactories) {
            Parameter[] result = new Parameter[parameters.length];

            for (int i = 0; i < result.length; i++) {
                result[i] = new Parameter(
                        parameters[i].getType(),
                        parameters[i].getParameterizedType(),
                        ArgumentRequirementFactory.createArgumentRequirements(node, parameters[i].getAnnotations(), argumentRequirementFactories),
                        getParamName(parameters[i])
                );
            }

            return result;
        }

        @ApiStatus.Internal
        private static String getParamName(java.lang.reflect.Parameter parameter) {
            String name = Optional.ofNullable(parameter.getAnnotation(Param.class))
                    .map(Param::value)
                    .orElse(null);

            if (name == null)
                return null;

            if (name.isEmpty())
                throw new IllegalArgumentException("Parameter name cannot be empty");

            return name;
        }

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegisteredVariant that)) return false;
        return priority == that.priority
                && Objects.equals(node, that.node)
                && Objects.equals(method, that.method)
                && Objects.deepEquals(parameters, that.parameters)
                && Objects.equals(argumentIndexList, that.argumentIndexList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, Arrays.hashCode(parameters), node, argumentIndexList, priority);
    }

    @Override
    public String toString() {
        return "RegisteredVariant{" + "method=" + method +
                ", parameters=" + Arrays.toString(parameters) +
                ", node=" + node +
                ", argumentIndexList=" + argumentIndexList +
                ", priority=" + priority +
                '}';
    }
}
