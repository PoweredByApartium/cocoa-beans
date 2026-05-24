package net.apartium.cocoabeans.reflect;

import net.apartium.cocoabeans.collect.ImmutableByteArrayList;
import org.jetbrains.annotations.ApiStatus;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * A {@link ClassLoader} that defines a fixed set of classes provided at construction time from an
 * in-memory mapping of fully-qualified binary name to JVM bytecode.
 * <p>
 * Every entry in the {@code classDefs} map passed to the constructor is eagerly defined in this
 * loader via {@link ClassLoader#defineClass(String, byte[], int, int)}. Subsequent lookups for
 * any of those classes through {@link #loadClass(String)} resolve to the {@link Class} defined
 * by this loader; any other name is delegated to the {@link #getParent() parent} loader using
 * the standard parent-first model inherited from {@link URLClassLoader}.
 * <p>
 * This loader exposes no URLs &mdash; only the classes supplied at construction are available
 * directly from it.
 *
 * @see URLClassLoader
 * @see ClassLoader#defineClass(String, byte[], int, int)
 *
 * @author Voigon (Lior S.)
 */
@ApiStatus.AvailableSince("0.0.50")
public class MapClassLoader extends URLClassLoader {

    private static final URL[] EMPTY_URLS_ARRAY = new URL[0];

    static {
        registerAsParallelCapable();
    }

    /**
     * Constructs a new {@code MapClassLoader} that eagerly defines every entry of
     * {@code classDefs} as a class loaded by this loader.
     *
     * @param parent    parent class loader for delegation; may be {@code null} to delegate to the
     *                  bootstrap class loader (see {@link ClassLoader#ClassLoader(ClassLoader)})
     * @param classDefs map of fully-qualified binary name (e.g. {@code "com.example.Foo"}) to the
     *                  bytecode for that class. The map is consumed at construction; later
     *                  modifications to the supplied map have no effect on this loader.
     * @throws ClassFormatError     if a bytecode entry is not a well-formed class file
     * @throws NoClassDefFoundError if a key in {@code classDefs} does not match the binary name
     *                              encoded inside the corresponding bytecode
     * @see ClassLoader#defineClass(String, byte[], int, int)
     */
    public MapClassLoader(ClassLoader parent, Map<String, ImmutableByteArrayList> classDefs) {
        super(EMPTY_URLS_ARRAY, parent);
        for (Map.Entry<String, ImmutableByteArrayList> entry : classDefs.entrySet()) {
            String name = entry.getKey();
            byte[] array = entry.getValue().toArray();
            defineClass(name, array, 0, array.length);
        }

    }

}
