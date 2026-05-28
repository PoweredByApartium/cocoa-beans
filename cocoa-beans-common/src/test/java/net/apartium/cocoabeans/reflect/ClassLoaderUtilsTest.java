package net.apartium.cocoabeans.reflect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * @author Voigon (Lior S.)
 */
class ClassLoaderUtilsTest {

    @Test
    void isValidJarNameAcceptsJarSuffix() {
        assertTrue(ClassLoaderUtils.isValidJarName("foo.jar"));
        assertTrue(ClassLoaderUtils.isValidJarName(".jar"));
        assertTrue(ClassLoaderUtils.isValidJarName("a.b.c.jar"));
    }

    @Test
    void isValidJarNameRejectsNonJar() {
        assertFalse(ClassLoaderUtils.isValidJarName("foo"));
        assertFalse(ClassLoaderUtils.isValidJarName("foo.zip"));
        assertFalse(ClassLoaderUtils.isValidJarName("foo.jar.bak"));
        assertFalse(ClassLoaderUtils.isValidJarName(""));
    }

    @Test
    void isValidJarNameIsCaseInsensitive() {
        assertTrue(ClassLoaderUtils.isValidJarName("foo.JAR"));
        assertTrue(ClassLoaderUtils.isValidJarName("foo.Jar"));
        assertTrue(ClassLoaderUtils.isValidJarName("foo.jAr"));
    }

    @Test
    void isJarFileRecognizesExistingJar(@TempDir Path tempDir) throws IOException {
        File jar = writeEmptyJar(tempDir, "sample.jar");
        assertTrue(ClassLoaderUtils.isJarFile(jar));
    }

    @Test
    void isJarFileRejectsDirectory(@TempDir Path tempDir) {
        // Directory is not a regular file, even if its name happens to end with .jar
        File dir = tempDir.toFile();
        assertFalse(ClassLoaderUtils.isJarFile(dir));
    }

    @Test
    void isJarFileRejectsNonExistent(@TempDir Path tempDir) {
        File missing = new File(tempDir.toFile(), "does-not-exist.jar");
        assertFalse(ClassLoaderUtils.isJarFile(missing));
    }

    @Test
    void isJarFileRejectsRegularFileWithWrongSuffix(@TempDir Path tempDir) throws IOException {
        File txt = Files.createFile(tempDir.resolve("plain.txt")).toFile();
        assertFalse(ClassLoaderUtils.isJarFile(txt));
    }

    @Test
    void appendToSearchPathAddsSingleJar(@TempDir Path tempDir) throws IOException {
        File jar = writeEmptyJar(tempDir, "lib.jar");

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            ClassLoaderUtils.appendToSearchPath(loader, jar);

            URL[] urls = loader.getURLs();
            assertEquals(1, urls.length);
            assertEquals(jar.toURI().toURL(), urls[0]);
        }
    }

    @Test
    void appendToSearchPathScansDirectoryForJars(@TempDir Path tempDir) throws IOException {
        File jarA = writeEmptyJar(tempDir, "a.jar");
        File jarB = writeEmptyJar(tempDir, "b.jar");
        Files.createFile(tempDir.resolve("README.txt"));

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            ClassLoaderUtils.appendToSearchPath(loader, tempDir.toFile());

            URL[] urls = loader.getURLs();
            assertEquals(2, urls.length, "expected only the two jars to be added; got: " + Arrays.toString(urls));

            List<URL> urlList = Arrays.asList(urls);
            assertTrue(urlList.contains(jarA.toURI().toURL()));
            assertTrue(urlList.contains(jarB.toURI().toURL()));
        }
    }

    @Test
    void appendToSearchPathRecursesIntoSubdirectories(@TempDir Path tempDir) throws IOException {
        Path nested = Files.createDirectory(tempDir.resolve("nested"));
        File outer = writeEmptyJar(tempDir, "outer.jar");
        File inner = writeEmptyJar(nested, "inner.jar");

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            ClassLoaderUtils.appendToSearchPath(loader, tempDir.toFile());

            List<URL> urls = Arrays.asList(loader.getURLs());
            assertEquals(2, urls.size());
            assertTrue(urls.contains(outer.toURI().toURL()));
            assertTrue(urls.contains(inner.toURI().toURL()));
        }
    }

    @Test
    void appendToSearchPathIgnoresSymlinkCycle(@TempDir Path tempDir) throws IOException {
        File jar = writeEmptyJar(tempDir, "lib.jar");
        Path nested = Files.createDirectory(tempDir.resolve("nested"));

        try {
            // a link pointing back into an ancestor - would recurse forever if followed
            Files.createSymbolicLink(nested.resolve("loop"), tempDir);
        } catch (IOException | UnsupportedOperationException e) {
            assumeTrue(false, "symbolic links unsupported on this platform: " + e);
        }

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            ClassLoaderUtils.appendToSearchPath(loader, tempDir.toFile());

            URL[] urls = loader.getURLs();
            assertEquals(1, urls.length, "symlink should be ignored; only the real jar added: " + Arrays.toString(urls));
            assertEquals(jar.toURI().toURL(), urls[0]);
        }
    }

    @Test
    void appendToSearchPathIgnoresSymlinkedJar(@TempDir Path tempDir) throws IOException {
        Path realDir = Files.createDirectory(tempDir.resolve("real"));
        File realJar = writeEmptyJar(realDir, "real.jar");
        Path scanDir = Files.createDirectory(tempDir.resolve("scan"));

        try {
            Files.createSymbolicLink(scanDir.resolve("link.jar"), realJar.toPath());
        } catch (IOException | UnsupportedOperationException e) {
            assumeTrue(false, "symbolic links unsupported on this platform: " + e);
        }

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            ClassLoaderUtils.appendToSearchPath(loader, scanDir.toFile());
            assertEquals(0, loader.getURLs().length, "symlinked jar should be ignored");
        }
    }

    @Test
    void appendToSearchPathOnEmptyDirectoryIsNoOp(@TempDir Path tempDir) throws IOException {
        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            ClassLoaderUtils.appendToSearchPath(loader, tempDir.toFile());
            assertEquals(0, loader.getURLs().length);
        }
    }

    @Test
    void appendToSearchPathRejectsNonJarFile(@TempDir Path tempDir) throws IOException {
        File txt = Files.createFile(tempDir.resolve("plain.txt")).toFile();

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            assertThrows(IllegalArgumentException.class,
                    () -> ClassLoaderUtils.appendToSearchPath(loader, txt));
            assertEquals(0, loader.getURLs().length);
        }
    }

    @Test
    void appendToSearchPathRejectsNonExistentFile(@TempDir Path tempDir) throws IOException {
        File missing = new File(tempDir.toFile(), "missing.jar");

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            // A path that doesn't exist isn't a directory and isn't a jar file, so it falls
            // through the directory branch and then fails the jar check
            assertThrows(IllegalArgumentException.class,
                    () -> ClassLoaderUtils.appendToSearchPath(loader, missing));
        }
    }

    @Test
    void appendToSearchPathMakesJarContentsResolvable(@TempDir Path tempDir) throws Exception {
        // Build a jar containing a real class file (SampleResource) and verify the loader
        // can find it as a resource through the extended search path
        String resourceName = "net/apartium/cocoabeans/reflect/ClassLoaderUtilsTest$SampleResource.class";
        byte[] classBytes = readClassBytes(SampleResource.class);

        File jar = writeJarWith(tempDir, "with-class.jar", resourceName, classBytes);

        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            assertNull(loader.findResource(resourceName));

            ClassLoaderUtils.appendToSearchPath(loader, jar);

            URL found = loader.findResource(resourceName);
            assertNotNull(found, "resource should be resolvable after the jar is on the search path");

            try (InputStream is = found.openStream()) {
                assertArrayEquals(classBytes, is.readAllBytes());
            }
        }
    }

    private static File writeEmptyJar(Path dir, String name) throws IOException {
        Path path = dir.resolve(name);
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(path))) {
            // empty jar
        }
        return path.toFile();
    }

    private static File writeJarWith(Path dir, String name, String entry, byte[] content) throws IOException {
        Path path = dir.resolve(name);
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(path))) {
            jos.putNextEntry(new JarEntry(entry));
            jos.write(content);
            jos.closeEntry();
        }
        return path.toFile();
    }

    private static byte[] readClassBytes(Class<?> clazz) throws IOException {
        String resource = clazz.getName().replace('.', '/') + ".class";
        try (InputStream is = clazz.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(is, "Resource not found on classpath: " + resource);
            return is.readAllBytes();
        }
    }

    public static class SampleResource {
        public String hi() {
            return "hi";
        }
    }

}
