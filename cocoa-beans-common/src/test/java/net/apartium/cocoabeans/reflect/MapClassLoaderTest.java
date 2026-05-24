package net.apartium.cocoabeans.reflect;

import net.apartium.cocoabeans.collect.ImmutableByteArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class MapClassLoaderTest {

    @Test
    void definesClassFromBytecode() throws Exception {
        String name = SampleClass.class.getName();
        ImmutableByteArrayList bytecode = readBytecode(SampleClass.class);

        MapClassLoader loader = new MapClassLoader(
                MapClassLoaderTest.class.getClassLoader(),
                Map.of(name, bytecode)
        );

        Class<?> loaded = loader.loadClass(name);

        // The class returned must be the one defined by our loader, not the test's parent loader
        assertSame(loader, loaded.getClassLoader());
        assertNotSame(SampleClass.class, loaded);
        assertEquals(name, loaded.getName());

        // And the defined class must be functional - instantiate and invoke a method
        Object instance = loaded.getDeclaredConstructor().newInstance();
        Object result = loaded.getMethod("greet").invoke(instance);
        assertEquals("hello", result);
    }

    @Test
    void definesMultipleClasses() throws Exception {
        String sampleName = SampleClass.class.getName();
        String otherName = OtherSampleClass.class.getName();

        MapClassLoader loader = new MapClassLoader(
                MapClassLoaderTest.class.getClassLoader(),
                Map.of(
                        sampleName, readBytecode(SampleClass.class),
                        otherName, readBytecode(OtherSampleClass.class)
                )
        );

        Class<?> sample = loader.loadClass(sampleName);
        Class<?> other = loader.loadClass(otherName);

        assertSame(loader, sample.getClassLoader());
        assertSame(loader, other.getClassLoader());
        assertEquals(42, other.getMethod("answer").invoke(other.getDeclaredConstructor().newInstance()));
    }

    @Test
    void emptyMapDefinesNoClasses() {
        MapClassLoader loader = new MapClassLoader(
                MapClassLoaderTest.class.getClassLoader(),
                Collections.emptyMap()
        );

        // Loading any class still delegates to the parent for unrelated names
        assertSame(String.class, assertDoesNotThrowLoad(loader, "java.lang.String"));
    }

    @Test
    void unknownClassFallsBackToParent() throws Exception {
        // Only define SampleClass; loading a class that wasn't supplied must delegate to parent
        MapClassLoader loader = new MapClassLoader(
                MapClassLoaderTest.class.getClassLoader(),
                Map.of(SampleClass.class.getName(), readBytecode(SampleClass.class))
        );

        Class<?> stringClass = loader.loadClass("java.lang.String");
        assertSame(String.class, stringClass);
    }

    @Test
    void mismatchedNameThrows() throws Exception {
        // Supplying SampleClass bytecode under the wrong name must blow up - the binary name
        // encoded inside the class file does not agree with the map key
        ImmutableByteArrayList bytecode = readBytecode(SampleClass.class);
        ClassLoader classLoader = MapClassLoaderTest.class.getClassLoader();
        Map<String, ImmutableByteArrayList> classDefs = Map.of("com.example.WrongName", bytecode);

        assertThrows(NoClassDefFoundError.class, () -> new MapClassLoader(classLoader, classDefs));
    }

    @Test
    void malformedBytecodeThrows() {
        ImmutableByteArrayList garbage = ImmutableByteArrayList.of((byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03);
        ClassLoader classLoader = MapClassLoaderTest.class.getClassLoader();
        Map<String, ImmutableByteArrayList> classDefs = Map.of(SampleClass.class.getName(), garbage);

        assertThrows(ClassFormatError.class, () -> new MapClassLoader(classLoader, classDefs));
    }

    @Test
    void nullParentDelegatesToBootstrap() throws Exception {
        // A null parent is valid (see ClassLoader(ClassLoader)) and routes delegation to the
        // bootstrap loader, which can still resolve JDK types like java.lang.String
        String name = SampleClass.class.getName();
        MapClassLoader loader = new MapClassLoader(null, Map.of(name, readBytecode(SampleClass.class)));

        Class<?> loaded = loader.loadClass(name);
        assertSame(loader, loaded.getClassLoader());

        assertSame(String.class, loader.loadClass("java.lang.String"));
    }

    private static Class<?> assertDoesNotThrowLoad(MapClassLoader loader, String name) {
        try {
            Class<?> result = loader.loadClass(name);
            assertNotNull(result);
            return result;
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Expected to load " + name, e);
        }
    }

    private static ImmutableByteArrayList readBytecode(Class<?> clazz) throws IOException {
        String resourcePath = clazz.getName().replace('.', '/') + ".class";
        ClassLoader cl = clazz.getClassLoader();
        try (InputStream is = cl.getResourceAsStream(resourcePath)) {
            assertNotNull(is, "Resource not found on classpath: " + resourcePath);
            byte[] bytes = is.readAllBytes();
            assertTrue(bytes.length > 0);
            return ImmutableByteArrayList.of(bytes);
        }
    }

    public static class SampleClass {
        public String greet() {
            return "hello";
        }
    }

    public static class OtherSampleClass {
        public int answer() {
            return 42;
        }
    }

    @Test
    void testVoidMethod() throws Throwable {
        String className = "dev.voigon.xxx.TestClass";
        ImmutableByteArrayList content = compileForJava8(className, """
                package dev.voigon.xxx;
                public class TestClass {
                    public static void doSomething() {
                        System.out.println("did something");
                    }
                }
                """);

        MapClassLoader loader = new MapClassLoader(getClass().getClassLoader(),
                Map.of(className, content));

        Class<?> clazz = Class.forName(className, true, loader);
        MethodHandle doSomething = MethodHandles.lookup().findStatic(clazz, "doSomething", MethodType.methodType(void.class));
        doSomething.invoke();
    }

    @Test
    void testMethodWithReturnValue() throws Throwable {
        String className = "dev.voigon.xxx.TestClass";
        ImmutableByteArrayList content = compileForJava8(className, """
                package dev.voigon.xxx;
                public class TestClass {
                    public static String getValue() {
                        return "A return value that needs to be preserved";
                    }
                }
                """);

        MapClassLoader loader = new MapClassLoader(getClass().getClassLoader(),
                Map.of(className, content));

        Class<?> clazz = Class.forName(className, true, loader);
        MethodHandle getValue = MethodHandles.lookup().findStatic(clazz, "getValue", MethodType.methodType(String.class));
        Assertions.assertEquals("A return value that needs to be preserved", getValue.invoke());
    }

    private static ImmutableByteArrayList compileForJava8(String className, String source) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "Test requires a JDK with javac available (got a JRE-only runtime)");

        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        JavaFileObject sourceFile = new SimpleJavaFileObject(
                URI.create("string:///" + simpleName + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };

        Map<String, ByteArrayOutputStream> emitted = new HashMap<>();
        StandardJavaFileManager standard = compiler.getStandardFileManager(null, null, null);
        JavaFileManager fileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(standard) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind, FileObject sibling) {
                return new SimpleJavaFileObject(URI.create("mem:///" + name.replace('.', '/') + kind.extension), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        emitted.put(name, os);
                        return os;
                    }
                };
            }
        };

        boolean success = compiler.getTask(null, fileManager, null,
                Arrays.asList("--release", "8"), null, Collections.singletonList(sourceFile)).call();
        fileManager.close();
        assertTrue(success, "In-memory compilation failed for " + className);

        ByteArrayOutputStream bytes = emitted.get(className);
        assertNotNull(bytes, "Compiler did not emit a class file for " + className);
        return ImmutableByteArrayList.of(bytes.toByteArray());
    }

}
