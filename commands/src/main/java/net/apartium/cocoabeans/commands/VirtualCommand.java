package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.reflect.MethodUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Virtual Command is a simple way to represent command while having the implementation else where
 */
@ApiStatus.AvailableSince("0.0.39")
public record VirtualCommand(
        String name,
        Set<String> aliases,
        CommandInfo info,
        Set<CommandVariant> variants
) implements GenericNode {

    public VirtualCommand(String name, Set<String> aliases, CommandInfo info, Set<CommandVariant> variants) {
        this.name = name;
        this.aliases = Set.copyOf(aliases);
        this.info = info;
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

        return new VirtualCommand(
                command.value(),
                Set.of(command.aliases()),
                info,
                getVariants(clazz)
        );
    }

    private static Set<CommandVariant> getVariants(Class<?> clazz) {
        Set<CommandVariant> variants = new HashSet<>();

        for (Method method : MethodUtils.getAllMethods(clazz)) {
            SubCommand[] subCommands = method.getAnnotationsByType(SubCommand.class);

            CommandInfo info = new CommandInfo();

            info.fromAnnotations(method.getAnnotations(), true);
            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method))
                info.fromAnnotations(targetMethod.getAnnotations(), false);

            for (SubCommand subCommand : subCommands)
                variants.add(new CommandVariant(
                        info,
                        subCommand.value(),
                        subCommand.ignoreCase()
                ));

            for (Method targetMethod : MethodUtils.getMethodsFromSuperClassAndInterface(method)) {
                subCommands = targetMethod.getAnnotationsByType(SubCommand.class);
                for (SubCommand subCommand : subCommands)
                    variants.add(new CommandVariant(
                            info,
                            subCommand.value(),
                            subCommand.ignoreCase()
                    ));
            }
        }

        return variants;
    }
}
