package net.apartium.cocoabeans.spigot.lazies;

import com.google.common.reflect.ClassPath;
import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.Ensures;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;

/**
 * Command auto registration for spigot.
 * @author Voigon
 * @see CommandExecutor
 */
public class CommandAutoRegistration {

    private final JavaPlugin
            plugin;

    private final boolean
            loadDevCommands;

    /**
     * Creates a new instance of command auto registration
     * @param plugin plugin instance to be associated with new commands
     * @param loadDevCommands should commands for dev servers should be loaded:
     * @see Command#devServer() indicates if a command should only be available on developer servers
     */
    public CommandAutoRegistration(JavaPlugin plugin, boolean loadDevCommands) {
        Ensures.notNull(plugin, "plugin +-");
        this.plugin = plugin;
        this.loadDevCommands = loadDevCommands;
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

            if (!CommandExecutor.class.isAssignableFrom(clazz))
                continue;

            Command annotation = clazz.getAnnotation(Command.class);
            if (annotation == null)
                continue;

            boolean devCommand = annotation.devServer();
            if (devCommand && !loadDevCommands)
                continue;

            CommandExecutor instance = createInstance(clazz);

            PluginCommand pluginCommand = plugin.getCommand(annotation.value());
            if (pluginCommand == null)
                plugin.getLogger().warning("Command /" + annotation.value() + " is not registered to this plugin!");
            else {
                pluginCommand.setExecutor(instance);
                plugin.getLogger().info("Loaded " + (devCommand ? "dev" : "") + " command /" + annotation.value() + "!");
            }

        }

    }

    private CommandExecutor createInstance(Class<?> clazz) {
        Constructor<?> constructor = clazz.getConstructors()[0];
        try {
            return (CommandExecutor) constructor.newInstance(constructor.getParameterCount() == 0 ? new Object[0] : new Object[] {plugin});
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
