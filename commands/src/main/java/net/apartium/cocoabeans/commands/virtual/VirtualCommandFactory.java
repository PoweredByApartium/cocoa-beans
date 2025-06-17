package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.reflect.ClassUtils;
import net.apartium.cocoabeans.reflect.MethodUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Used to construct instances of {@link VirtualCommandDefinition}
 * @see VirtualCommandDefinition
 * @see VirtualMetadata
 */
@ApiStatus.AvailableSince("0.0.39")
public class VirtualCommandFactory {

    private final List<BiConsumer<AnnotatedElement, Map<String, Object>>> mappers = new ArrayList<>();

    /**
     * Enrich all virtual commands created through this factory with metadata by adding it in the mapper parameter
     * <br/>
     * <br/>Example of usage:
     * <pre>{@code
     * VirtualCommandFactory factory = ...;
     * factory.addMetadataMapper((element, metadata) -> {
     *   Permission permission = element.getAnnotation(Permission.class);
     *   if (permission != null)
     *      metadata.put("permission", permission.value());
     * });
     * }</pre>
     * @param mapper mapping annotated element into metadata
     */
    public void addMetadataMapper(BiConsumer<AnnotatedElement, Map<String, Object>> mapper) {
        mappers.add(mapper);
    }

    /**
     * Map a command node into a virtual command
     * @param nodes command nodes to represent
     * @return a new virtual command instance
     */
    public VirtualCommandDefinition create(CommandNode... nodes) {
        if (nodes.length == 0)
            return null;


        Class<?> baseClass = nodes[0].getClass();
        Command baseCommand = baseClass.getAnnotation(Command.class);
        if (baseCommand == null)
            return null;

        for (int i = 1; i < nodes.length; i++) {
            Class<?> clazz = nodes[i].getClass();
            Command command = clazz.getAnnotation(Command.class);
            if (command == null)
                throw new IllegalArgumentException("Command " + clazz.getName() + " is not annotated with @Command");

            if (!baseCommand.value().equals(command.value()))
                throw new IllegalArgumentException("Command name aren't same \nExpected: " + baseCommand.value() + "\nActual: " + command.value());

            if (!CollectionHelpers.equalsArray(baseCommand.aliases(), command.aliases()))
                throw new IllegalArgumentException("Command alias aren't same \nExpected: " + Arrays.toString(baseCommand.aliases()) + "\nActual: " + Arrays.toString(command.aliases()));
        }

        List<Class<?>> classes = Arrays.stream(nodes).map(Object::getClass).collect(Collectors.toList());
        return new VirtualCommandDefinition(
                baseCommand.value(),
                Set.of(baseCommand.aliases()),
                CommandInfo.createFromAnnotations(classes.stream().map(Class::getAnnotations).toList()),
                getVariants(classes),
                getMetadata(classes)
        );
    }

    protected void metaDataMap(AnnotatedElement element, Map<String, Object> metadata) {
        for (BiConsumer<AnnotatedElement, Map<String, Object>> mapper : mappers)
            mapper.accept(element, metadata);

        for (VirtualMetadata virtualMetadata : element.getAnnotationsByType(VirtualMetadata.class))
            metadata.put(virtualMetadata.key(), virtualMetadata.value());
    }

    protected Map<String, Object> getMetadata(Collection<Class<?>> classes) {
        Map<String, Object> metadata = new HashMap<>();

        for (Class<?> clazz : classes) {
            for (Class<?> targetClass : ClassUtils.getSuperClassAndInterfaces(clazz))
                metaDataMap(targetClass, metadata);
        }

        return metadata;
    }

    protected Map<String, Object> getMetadata(Method method) {
        Map<String, Object> metadata = new HashMap<>();

        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
            metaDataMap(targetMethod, metadata);

        metaDataMap(method, metadata);
        return metadata;
    }

    protected Set<VirtualCommandVariant> getVariants(Collection<Class<?>> classes) {
        Set<VirtualCommandVariant> variants = new HashSet<>();
        for (Class<?> clazz : classes) {
            for (Method method : MethodUtils.getAllMethods(clazz))
                getVariants(method, variants);
        }
        return variants;
    }

    protected void getVariants(Method method, Set<VirtualCommandVariant> variants) {
        SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

        List<Annotation[]> annotations = new ArrayList<>();
        annotations.add(method.getAnnotations());
        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
            annotations.add(targetMethod.getAnnotations());

        Map<String, Object> metadata = null;
        CommandInfo info = null;
        for (SubCommand subCommand : subCommands)
            variants.add(new VirtualCommandVariant(
                    info == null ? info = CommandInfo.createFromAnnotations(annotations) : info,
                    subCommand.value(),
                    subCommand.ignoreCase(),
                    metadata == null ? metadata = getMetadata(method) : metadata
            ));

        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            subCommands = targetMethod.getAnnotationsByType(SubCommand.class);
            for (SubCommand subCommand : subCommands)
                variants.add(new VirtualCommandVariant(
                        info == null ? info = CommandInfo.createFromAnnotations(annotations) : info,
                        subCommand.value(),
                        subCommand.ignoreCase(),
                        metadata == null ? metadata = getMetadata(method) : metadata
                ));
        }

    }


    
}
