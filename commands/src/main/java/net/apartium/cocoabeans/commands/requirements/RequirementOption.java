package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.GenericNode;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class RequirementOption {

    private final String className;
    private final Map<String, Object> arguments;

    public RequirementOption(String className, Map<String, Object> arguments) {
        this.className = className;
        this.arguments = Map.copyOf(arguments);
    }

    public String getClassName() {
        return className;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public Requirement createRequirement(GenericNode node, Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories, Map<Class<? extends Annotation>, RequirementFactory> externalRequirementFactories) {
        try {
            return RequirementFactory.getRequirement(node, createAnnotation(), requirementFactories, externalRequirementFactories);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Annotation createAnnotation() throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);

        if (!clazz.isAnnotation()) {
            throw new IllegalArgumentException(className + " is not an annotation type");
        }

        Class<? extends Annotation> annotationType = clazz.asSubclass(Annotation.class);

        InvocationHandler handler = new AnnotationInvocationHandler(annotationType, arguments);

        // noinspection unchecked
        return (Annotation) Proxy.newProxyInstance(
                annotationType.getClassLoader(),
                new Class[]{annotationType},
                handler
        );
    }

    public static Set<Requirement> getRequirements(Set<RequirementOption> options, GenericNode node, Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories, Map<Class<? extends Annotation>, RequirementFactory> externalRequirementFactories) {
        return options.stream()
                .map(option -> option.createRequirement(node, requirementFactories, externalRequirementFactories))
                .collect(Collectors.toSet());
    }

    private static class AnnotationInvocationHandler implements InvocationHandler {
        private final Class<? extends Annotation> annotationType;
        private final Map<String, Object> values;

        AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values) {
            this.annotationType = annotationType;
            this.values = new HashMap<>(values);

            for (Method method : annotationType.getDeclaredMethods()) {
                if (!values.containsKey(method.getName()))
                    values.put(method.getName(), method.getDefaultValue());
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if (name.equals("annotationType")) {
                return annotationType;
            } else if (name.equals("toString")) {
                return "@" + annotationType.getName() + values;
            } else if (name.equals("hashCode")) {
                return values.hashCode();
            } else if (name.equals("equals")) {
                Object other = args[0];
                return proxy == other || (other != null && proxy.toString().equals(other.toString()));
            }
            return values.get(name);
        }
    }

    public static RequirementOption create(Annotation annotation) {
        Class<? extends Annotation> type = annotation.annotationType();
        if (type.getAnnotation(CommandRequirementType.class)== null)
            return null;

        Map<String, Object> arguments = new HashMap<>();

        for (Method method : type.getDeclaredMethods()) {
            try {
                Object value = method.invoke(annotation);
                arguments.put(method.getName(), value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to read annotation value", e);
            }
        }

        return new RequirementOption(type.getName(), arguments);
    }

    @Override
    public String toString() {
        return "RequirementOption{" +
                "className='" + className + '\'' +
                ", arguments=" + arguments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RequirementOption that = (RequirementOption) o;
        return Objects.equals(className, that.className) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, arguments);
    }
}
