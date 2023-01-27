/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot.lazies;

import com.google.common.reflect.ClassPath;
import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.spigot.Commands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Command auto registration for spigot.
 * @author Voigon
 * @see CommandExecutor
 */
public class CommandAutoRegistration {

    private final JavaPlugin
            plugin;

    boolean
            allowDirectCommandMapRegistration,
            loadDevCommands;

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

        Set<ClassPath.ClassInfo> searchScope = deep ? classPath.getTopLevelClassesRecursive(packageName) : classPath.getTopLevelClasses(packageName);
        for (ClassPath.ClassInfo classInfo : searchScope)
            loadCommand(classLoader, classInfo);

    }

    private void loadCommand(ClassLoader classLoader, ClassPath.ClassInfo classInfo) {
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(classInfo.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (!CommandExecutor.class.isAssignableFrom(clazz))
            return;

        Command annotation = clazz.getAnnotation(Command.class);
        if (annotation == null)
            return;

        boolean devCommand = annotation.devServer();
        if (devCommand && !loadDevCommands)
            return;

        CommandExecutor instance = createInstance(clazz);
        PluginCommand pluginCommand = plugin.getCommand(annotation.value());
        org.bukkit.command.Command finalCommand = null;

        if (pluginCommand == null) {
            if (this.allowDirectCommandMapRegistration) {
                finalCommand = new DelegatingCommand(annotation, instance);
                if (annotation.aliases().length > 0)
                    finalCommand.setAliases(Arrays.asList(annotation.aliases()));

                Commands.getCommandMap().register(annotation.value(), plugin.getName().toLowerCase(Locale.ROOT), finalCommand);

            } else {
                plugin.getLogger().warning("Command /" + annotation.value() + " is not registered to this plugin!");
                return;
            }
        } else {
            finalCommand = pluginCommand;
            pluginCommand.setExecutor(instance);
            plugin.getLogger().info("Loaded " + (devCommand ? "dev" : "") + " command /" + annotation.value() + "!");
        }

        if (!annotation.permission().isEmpty())
            finalCommand.setPermission(annotation.permission());

        if (!annotation.description().isEmpty())
            finalCommand.setDescription(annotation.description());


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
         * Set permission to be required for every player executing that command
         */
        String permission() default "";

        /**
         * Command aliases
         */
        String[] aliases() default {};

        /**
         * Command description
         */
        String description() default "";

        /**
         *
         * @return true if this command should be enabled only if loadDevCommands param is passed as true
         */
        boolean devServer() default false;

    }

    private static class DelegatingCommand extends org.bukkit.command.Command {

        private final CommandExecutor instance;

        public DelegatingCommand(Command annotation, CommandExecutor instance) {
            super(annotation.value());
            this.instance = instance;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return instance.onCommand(sender, this, commandLabel, args);
        }

        @NotNull
        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            if (instance instanceof TabCompleter tabCompleter) {
                List<String> result = tabCompleter.onTabComplete(sender, this, alias, args);
                return result == null ? List.of() : result;
            }

            return super.tabComplete(sender, alias, args);
        }
    }
}
