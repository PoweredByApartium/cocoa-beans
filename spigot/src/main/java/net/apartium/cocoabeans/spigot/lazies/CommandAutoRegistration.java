package net.apartium.cocoabeans.spigot.lazies;

import com.google.common.reflect.ClassPath;
import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.commands.CommandManager;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.spigot.Commands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.Locale;

/**
 * Command auto registration for spigot.
 * @author Voigon
 * @see CommandExecutor
 */
public class CommandAutoRegistration {

    private static Class<?> COMMAND_NODE_CLASS;

    static {
        try {
            COMMAND_NODE_CLASS = Class.forName("net.apartium.cocoabeans.commands.CommandNode");
        } catch (ClassNotFoundException e) {
            COMMAND_NODE_CLASS = null;
        }
    }

    private final JavaPlugin
            plugin;

    boolean
            allowDirectCommandMapRegistration,
            loadDevCommands;

    CommandManager
            commandManager;

    /**
     * Creates a new instance of command auto registration
     * @param plugin plugin instance to be associated with new commands
     */
    public CommandAutoRegistration(JavaPlugin plugin) {
        Ensures.notNull(plugin, "plugin +-");
        this.plugin = plugin;
    }

    /**
     * Indicates whether to enable dev commands or not. default value is false.
     * @param value should commands for dev servers should be loaded:
     * @return current instance
     * @see Command#devServer() indicates if a command should only be available on developer servers
     */
    public CommandAutoRegistration enableDevCommands(boolean value) {
        this.loadDevCommands = value;
        return this;
    }

    public CommandAutoRegistration setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        return this;
    }

    /**
     * Admits you are a lazy ass developer and lets you register command without putting them in your plugin.yml file.
     * By default, this is disabled since everyone should be given a chance.
     * @return current instance
     */
    public CommandAutoRegistration iAmALazyAssDeveloper() {
        this.allowDirectCommandMapRegistration = true;
        return this;
    }

    /**
     * Auto discovers command in given package name and its subpackages
     * @param packageName package name
     */
    public void register(String packageName) {
        register(packageName, true);

    }

    /**
     * Auto discovers command in given package name
     * @param packageName package name
     * @param deep whether sub packages of given package should also be queried
     */
    public void register(String packageName, boolean deep) {
        Ensures.notEmpty(packageName, "plugin +-");

        ClassLoader classLoader = plugin.getClass().getClassLoader();
        ClassPath classPath;
        try {
            classPath = ClassPath.from(classLoader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (ClassPath.ClassInfo classInfo : deep ? classPath.getTopLevelClassesRecursive(packageName) : classPath.getTopLevelClasses(packageName)) {
            Class<?> clazz;
            try {
                clazz = classLoader.loadClass(classInfo.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            if (COMMAND_NODE_CLASS != null && COMMAND_NODE_CLASS.isAssignableFrom(clazz)) {
                handleCommandNode(clazz);
                continue;
            }

            if (!CommandExecutor.class.isAssignableFrom(clazz))
                continue;

            Command annotation = clazz.getAnnotation(Command.class);
            if (annotation == null)
                continue;

            boolean devCommand = annotation.devServer();
            if (devCommand && !loadDevCommands)
                continue;

            CommandExecutor instance = (CommandExecutor) createInstance(clazz);

            PluginCommand pluginCommand = plugin.getCommand(annotation.value());
            if (pluginCommand == null) {
                if (this.allowDirectCommandMapRegistration) {
                    Commands.getCommandMap().register(annotation.value(), plugin.getName().toLowerCase(Locale.ROOT), new org.bukkit.command.Command(annotation.value()) {

                        @Override
                        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                            return instance.onCommand(sender, this, commandLabel, args);
                        }

                    });

                } else
                    plugin.getLogger().warning("Command /" + annotation.value() + " is not registered to this plugin!");
            } else {
                pluginCommand.setExecutor(instance);
                plugin.getLogger().info("Loaded " + (devCommand ? "dev" : "") + " command /" + annotation.value() + "!");
            }

        }

    }

    private void handleCommandNode(Class<?> clazz) {
        if (clazz.getAnnotation(net.apartium.cocoabeans.commands.Command.class) == null) {
            plugin.getLogger().warning("Command class " + clazz.getName() + " is not annotated with @Command!");
            return;
        }

        Command annotation = clazz.getAnnotation(Command.class);
        if (annotation != null && annotation.devServer() && !loadDevCommands) {
            plugin.getLogger().warning(clazz.getName() + " wasn't registered to this plugin because it's a dev command!");
            return;
        }

        CommandNode node = (CommandNode) createInstance(clazz);
        commandManager.addCommand(node);
    }

    private Object createInstance(Class<?> clazz) {
        Constructor<?> constructor = clazz.getConstructors()[0];
        try {
            return constructor.newInstance(constructor.getParameterCount() == 0 ? new Object[0] : new Object[] {plugin});
        } catch (Exception e) {
            Dispensers.dispense(e);
            return null;
        }
    }

    /**
     * Annotation to indicate a command class
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {

        /**
         * @return The command name, without a slash '/'
         */
        String value();

        /**
         *
         * @return true if this command should be enabled only if loadDevCommands param is passed as true
         */
        boolean devServer() default false;

    }

}
