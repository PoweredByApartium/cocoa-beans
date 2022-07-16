package net.apartium.cocoabeans.spigot.lazies;

import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;

/**
 * Listener auto registration for spigot.
 * @author Voigon
 * @see Listener
 */
public class ListenerAutoRegistration {

    final JavaPlugin
            plugin;

    final boolean
            loadDevListeners;

    /**
     * Creates a new instance of listener auto registration
     * @param plugin plugin instance to be associated with new listeners
     * @param loadDevListeners should listeners for dev servers should be loaded:
     * @see DevServerListener indicates if a command should only be available on developer servers
     */
    public ListenerAutoRegistration(JavaPlugin plugin, boolean loadDevListeners) {
        this.plugin = plugin;
        this.loadDevListeners = loadDevListeners;

    }

    /**
     * Auto discovers listeners in given package name and its subpackages
     * @param packageName package name
     */
    public void register(String packageName) {
        register(packageName, true);

    }

    /**
     * Auto discovers listeners in given package name
     * @param packageName package name
     * @param deep whether sub packages of given package should also be queried
     */
    public void register(String packageName, boolean deep) {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        ClassPath classPath;
        try {
            classPath = ClassPath.from(classLoader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (ClassPath.ClassInfo classInfo : deep ? classPath.getTopLevelClassesRecursive(packageName) : classPath.getTopLevelClasses(packageName)) {
            try {
                Class<?> clazz = classLoader.loadClass(classInfo.getName());
                if (!Listener.class.isAssignableFrom(clazz))
                    continue;

                boolean devListener = clazz.isAnnotationPresent(DevServerListener.class);
                if (devListener && !loadDevListeners)
                    continue;

                Constructor<?> constructor = clazz.getConstructors()[0];
                Listener instance = (Listener) constructor.newInstance(constructor.getParameterCount() == 0 ? new Object[0] : new Object[] {plugin});
                Bukkit.getPluginManager().registerEvents(instance, plugin);

                plugin.getLogger().info("Loaded " + (devListener ? "dev" : "") + " listener " + clazz.getSimpleName() + "!");

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DevServerListener {
    }

}
