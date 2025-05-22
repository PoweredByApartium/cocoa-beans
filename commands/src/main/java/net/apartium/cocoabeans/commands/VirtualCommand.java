package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.RequirementOption;
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
public record VirtualCommand(
        String name,
        Set<String> aliases,
        CommandInfo info,
        Set<RequirementOption> requirements,
        Set<CommandVariant> variants
) implements GenericNode {

    public VirtualCommand(String name, Set<String> aliases, CommandInfo info, Set<RequirementOption> requirements, Set<CommandVariant> variants) {
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
        Class<? extends CommandNode> clazz = node.getClass();
        Command command = clazz.getAnnotation(Command.class);
        if (command == null)
            return null;


        CommandInfo info = new CommandInfo();
        info.fromAnnotations(clazz.getAnnotations(), false);

        Set<RequirementOption> requirements = new HashSet<>();
        for (Class<?> c : ClassUtils.getSuperClassAndInterfaces(clazz)) {
            for (Annotation annotation : c.getAnnotations()) {
                RequirementOption option = RequirementOption.create(annotation);
                if (option == null)
                    continue;

                requirements.add(option);
            }
        }

        return new VirtualCommand(
                command.value(),
                Set.of(command.aliases()),
                info,
                requirements,
                getVariants(clazz)
        );
    }

    private static Set<CommandVariant> getVariants(Class<?> clazz) {
        Set<CommandVariant> variants = new HashSet<>();

        for (Method method : MethodUtils.getAllMethods(clazz)) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

            Set<RequirementOption> methodRequirement = findAllRequirements(method);
            CommandInfo info = new CommandInfo();

            info.fromAnnotations(method.getAnnotations(), true);
            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
                info.fromAnnotations(targetMethod.getAnnotations(), false);

            for (SubCommand subCommand : subCommands)
                variants.add(new CommandVariant(
                        methodRequirement,
                        info,
                        subCommand.value(),
                        subCommand.ignoreCase()
                ));

            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
                subCommands = targetMethod.getAnnotationsByType(SubCommand.class);
                for (SubCommand subCommand : subCommands)
                    variants.add(new CommandVariant(
                            methodRequirement,
                            info,
                            subCommand.value(),
                            subCommand.ignoreCase()
                    ));
            }
        }

        return variants;
    }

    private static Set<RequirementOption> findAllRequirements(Method method) {
        Set<RequirementOption> requirements = new HashSet<>();

        for (Annotation annotation : method.getAnnotations()) {
            RequirementOption option = RequirementOption.create(annotation);
            if (option == null)
                continue;

            requirements.add(option);
        }

        for (Method target : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
            for (Annotation annotation : target.getAnnotations()) {
                RequirementOption option = RequirementOption.create(annotation);
                if (option == null)
                    continue;

                requirements.add(option);
            }
        }

        return requirements;
    }
}
