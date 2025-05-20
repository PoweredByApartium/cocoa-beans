package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementFactory;
import net.apartium.cocoabeans.reflect.ClassUtils;
import net.apartium.cocoabeans.reflect.MethodUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Virtual Command is a simple way to represent command while having the implementation else where
 * @param requirements Change to requirement as more simple type
 */
@ApiStatus.AvailableSince("0.0.39")
public record VirtualCommand(String name, Set<String> aliases, CommandInfo info, Set<Requirement> requirements, Set<CommandVariant> variants) implements GenericNode {

    public VirtualCommand(String name, Set<String> aliases, CommandInfo info, Set<Requirement> requirements, Set<CommandVariant> variants) {
        this.name = name;
        this.aliases = Set.copyOf(aliases);
        this.info = info;
        this.requirements = Set.copyOf(requirements);
        this.variants = Set.copyOf(variants);
    }

    /**
     * Create virtual command from command node
     * @param node the node to create as virtual command
     * @return new virtual command base on node
     */
    public static VirtualCommand create(CommandNode node) {
        return create(node, new HashMap<>(), new HashMap<>());
    }

    /**
     * Create virtual command from command node
     * @param node the node to create as virtual command
     * @param requirementFactories caching of the requirement factory creation
     * @param externalRequirementFactories external requirement factories that will be used instead of the normal one
     * @return new virtual command base on node
     */
    public static VirtualCommand create(CommandNode node, Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories, Map<Class<? extends Annotation>, RequirementFactory> externalRequirementFactories) {
        Class<? extends CommandNode> clazz = node.getClass();
        Command command = clazz.getAnnotation(Command.class);
        if (command == null)
            return null;


        CommandInfo info = new CommandInfo();
        info.fromAnnotations(clazz.getAnnotations(), false);

        Set<Requirement> requirements = new HashSet<>();
        for (Class<?> c : ClassUtils.getSuperClassAndInterfaces(clazz))
            requirements.addAll(RequirementFactory.createRequirementSet(node, c.getAnnotations(), requirementFactories, externalRequirementFactories));

        return new VirtualCommand(
                command.value(),
                Set.of(command.aliases()),
                info,
                requirements,
                getVariants(node, clazz, requirementFactories, externalRequirementFactories)
        );
    }

    private static Set<CommandVariant> getVariants(CommandNode node,Class<?> clazz, Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories, Map<Class<? extends Annotation>, RequirementFactory> externalRequirementFactories) {
        Set<CommandVariant> variants = new HashSet<>();

        for (Method method : MethodUtils.getAllMethods(clazz)) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

            Set<Requirement> methodRequirement = findAllRequirements(node, method, requirementFactories, externalRequirementFactories);
            CommandInfo info = new CommandInfo();

            info.fromAnnotations(method.getAnnotations(), true);
            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
                info.fromAnnotations(targetMethod.getAnnotations(), false);

            for (SubCommand subCommand : subCommands)
                variants.add(new CommandVariant(
                        methodRequirement,
                        info,
                        subCommand
                ));

            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
                subCommands = targetMethod.getAnnotationsByType(SubCommand.class);
                for (SubCommand subCommand : subCommands)
                    variants.add(new CommandVariant(
                            methodRequirement,
                            info,
                            subCommand
                    ));
            }
        }

        return variants;
    }

    private static Set<Requirement> findAllRequirements(CommandNode node, Method method, Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories, Map<Class<? extends Annotation>, RequirementFactory> externalRequirementFactories) {
        Set<Requirement> requirements = new HashSet<>(RequirementFactory.createRequirementSet(node, method.getAnnotations(), requirementFactories, externalRequirementFactories));
        for (Method target : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            requirements.addAll(RequirementFactory.createRequirementSet(node, target.getAnnotations(), requirementFactories, externalRequirementFactories));
        }

        return requirements;
    }
}
