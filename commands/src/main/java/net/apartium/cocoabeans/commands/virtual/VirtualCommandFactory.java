package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.reflect.ClassUtils;
import net.apartium.cocoabeans.reflect.MethodUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

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
     * @param node command node to represent
     * @return a new virtual command instance
     */
    public VirtualCommandDefinition create(CommandNode node) {
        Class<?> clazz = node.getClass();
        Command command = clazz.getAnnotation(Command.class);
        if (command == null)
            return null;

        CommandInfo info = new CommandInfo();
        info.fromAnnotations(clazz.getAnnotations(), false);

        return new VirtualCommandDefinition(
                command.value(),
                Set.of(command.aliases()),
                info,
                getVariants(clazz),
                getMetadata(clazz)
        );
    }

    protected void metaDataMap(AnnotatedElement element, Map<String, Object> metadata) {
        for (BiConsumer<AnnotatedElement, Map<String, Object>> mapper : mappers)
            mapper.accept(element, metadata);

        for (VirtualMetadata virtualMetadata : element.getAnnotationsByType(VirtualMetadata.class))
            metadata.put(virtualMetadata.key(), virtualMetadata.value());
    }

    protected Map<String, Object> getMetadata(Class<?> clazz) {
        Map<String, Object> metadata = new HashMap<>();

        for (Class<?> targetClass : ClassUtils.getSuperClassAndInterfaces(clazz))
            metaDataMap(targetClass, metadata);

        return metadata;
    }

    protected Map<String, Object> getMetadata(Method method) {
        Map<String, Object> metadata = new HashMap<>();

        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
            metaDataMap(targetMethod, metadata);

        metaDataMap(method, metadata);
        return metadata;
    }

    protected Set<VirtualCommandVariant> getVariants(Class<?> clazz) {
        Set<VirtualCommandVariant> variants = new HashSet<>();

        for (Method method : MethodUtils.getAllMethods(clazz))
            getVariants(method, variants);

        return variants;
    }

    protected void getVariants(Method method, Set<VirtualCommandVariant> variants) {
        SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

        CommandInfo info = new CommandInfo();

        info.fromAnnotations(method.getAnnotations(), true);
        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
            info.fromAnnotations(targetMethod.getAnnotations(), false);

        Map<String, Object> metadata = null;
        for (SubCommand subCommand : subCommands)
            variants.add(new VirtualCommandVariant(
                    info,
                    subCommand.value(),
                    subCommand.ignoreCase(),
                    metadata == null ? metadata = getMetadata(method) : metadata
            ));

        for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            subCommands = targetMethod.getAnnotationsByType(SubCommand.class);
            for (SubCommand subCommand : subCommands)
                variants.add(new VirtualCommandVariant(
                        info,
                        subCommand.value(),
                        subCommand.ignoreCase(),
                        metadata == null ? metadata = getMetadata(method) : metadata
                ));
        }

    }


    
}
