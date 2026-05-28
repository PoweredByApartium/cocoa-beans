/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.reflect;

import net.apartium.cocoabeans.Dispensers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Locale;

/**
 * Helpers for working with {@link ClassLoader} instances at runtime.
 * <p>
 * Currently focused on extending a {@link URLClassLoader}'s search path with additional jar
 * files. The {@code URLClassLoader#addURL(URL)} method this relies on is {@code protected}, so it
 * is reached via {@link MethodHandle reflective access} resolved once in a class initializer.
 * <p>
 * <b>Module access:</b> on Java&nbsp;9+ the reflective access used here requires the
 * {@code java.net} package of the {@code java.base} module to be opened to the caller, e.g. via
 * the JVM flag
 * <pre>--add-opens java.base/java.net=ALL-UNNAMED</pre>
 * Without it, the static initializer of this class will fail.
 *
 * @see URLClassLoader
 * @author Voigon (Lior S.)
 */
@ApiStatus.AvailableSince("0.0.50")
public class ClassLoaderUtils {

    private static MethodHandle
            addUrl;

    static {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            addUrl = MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            Dispensers.dispense(e);
        }
    }

    /**
     * Appends a jar file &mdash; or every jar inside a directory tree &mdash; to the search path
     * of the given {@link URLClassLoader}, making any classes and resources inside them
     * resolvable through that loader.
     * <p>
     * If {@code file} is a directory, its direct children are processed recursively; non-jar
     * entries inside a directory are silently ignored. If {@code file} is a regular file it must
     * be a jar (see {@link #isJarFile(File)}); otherwise an {@link IllegalArgumentException} is
     * thrown.
     * <p>
     * Symbolic links are ignored, so cyclic links (a link pointing back into an ancestor) cannot
     * trigger infinite recursion. If a directory cannot be listed (an I/O error, e.g. missing read
     * permission) the failure is surfaced rather than silently ignored.
     * <p>
     * Adding a URL only extends what the loader can find later &mdash; no class is actually
     * loaded or initialized as a side effect.
     *
     * @param classLoader the loader whose search path should be extended; must not be {@code null}
     * @param file        a jar file, or a directory tree to scan for jar files; must not be {@code null}
     * @throws IllegalArgumentException if {@code file} is a regular file but not a jar
     * @throws IllegalStateException    if a directory in the tree cannot be listed
     */
    public static void appendToSearchPath(@NotNull URLClassLoader classLoader, @NotNull File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles(f -> !Files.isSymbolicLink(f.toPath()) && (f.isDirectory() || isJarFile(f)));
            if (files == null)
                throw new IllegalStateException("failed to list directory contents: " + file);

            for (File f : files)
                appendToSearchPath(classLoader, f);

        } else {
            if (!isJarFile(file))
                throw new IllegalArgumentException("file specified is not a jar file");

            try {
                addUrl.invoke(classLoader, file.toURI().toURL());

            } catch (Throwable e ){
                Dispensers.dispense(e);
            }
        }
    }

    /**
     * Tells whether {@code file} is an existing regular file whose name identifies it as a jar.
     *
     * @param file file to inspect
     * @return {@code true} iff {@code file} exists as a regular file and its name passes
     *         {@link #isValidJarName(String)}
     */
    public static boolean isJarFile(File file) {
        return file.isFile() && isValidJarName(file.getName());
    }

    /**
     * Tells whether {@code fileName} matches the expected naming convention for a jar archive.
     * Matching is case-insensitive against the {@code .jar} suffix; the file system is not
     * consulted.
     *
     * @param fileName file name (not a full path)
     * @return {@code true} iff {@code fileName} ends with {@code .jar} (any case)
     */
    public static boolean isValidJarName(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).endsWith(".jar");
    }

    private ClassLoaderUtils() {}

}
