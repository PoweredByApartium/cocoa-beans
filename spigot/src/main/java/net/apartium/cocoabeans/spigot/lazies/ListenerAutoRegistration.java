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
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
        register(packageName, deep, List.of(), Set.of());
    }

    /**
     * Auto discovers listeners in given package name
     * @param packageName package name
     * @param deep whether sub packages of given package should also be queried
     * @param filler filler are object that could be provided in constructor of listeners
     * @param ignore ignored classes
     */
    public void register(String packageName, boolean deep, List<Object> filler, Set<Class<?>> ignore) {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        ClassPath classPath;
        try {
            classPath = ClassPath.from(classLoader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        filler = new ArrayList<>(filler);
        filler.add(plugin);

        for (ClassPath.ClassInfo classInfo : deep ? classPath.getTopLevelClassesRecursive(packageName) : classPath.getTopLevelClasses(packageName)) {
            try {
                Class<?> clazz = classLoader.loadClass(classInfo.getName());
                if (!Listener.class.isAssignableFrom(clazz))
                    continue;

                if (ignore.contains(clazz))
                    continue;

                boolean devListener = clazz.isAnnotationPresent(DevServerListener.class);
                if (devListener && !loadDevListeners)
                    continue;

                Constructor<?> constructor = clazz.getConstructors()[0];
                Listener instance = createInstance(constructor, filler);

                if (instance == null) {
                    Bukkit.getLogger().warning("Failed to create listener " + clazz.getSimpleName() + "!");
                    return;
                }

                Bukkit.getPluginManager().registerEvents(instance, plugin);

                Bukkit.getLogger().info("Loaded " + (devListener ? "dev " : "") + "listener " + clazz.getSimpleName() + "!");

            } catch (Exception e) {
                Dispensers.dispense(e);
            }
        }

    }

    private Listener createInstance(Constructor<?> constructor, List<Object> filler) {

        if (constructor.getParameterCount() == 0) {
            try {
                return (Listener) constructor.newInstance();
            } catch (Exception e) {
                return null;
            }
        }

        filler = new ArrayList<>(filler);

        Object[] objects = new Object[constructor.getParameterCount()];
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            Class<?> target = constructor.getParameterTypes()[i];

            Object value = null;

            for (Object o : filler) {
                if (target.isInstance(o)) {
                    value = o;
                    break;
                }
            }

            if (value == null)
                return null;

            filler.remove(value);
            objects[i] = value;
        }

        try {
            return (Listener) constructor.newInstance(objects);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Indicates a listener most only be registered if {@link ListenerAutoRegistration#loadDevListeners} is set to true
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DevServerListener {
    }

}
