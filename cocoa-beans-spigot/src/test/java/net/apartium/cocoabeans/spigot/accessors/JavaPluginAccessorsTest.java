package net.apartium.cocoabeans.spigot.accessors;

import net.apartium.cocoabeans.spigot.SpigotTestBase;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class JavaPluginAccessorsTest extends SpigotTestBase {

    @Override
    public void initialize() {

    }

    @Test
    void getPluginFile() {
        File file = JavaPluginAccessors.getPluginFile(plugin);
        assertNotNull(file);
        assertEquals("MockPlugin-1.0.0.jar", file.getName());

    }

    @Test
    void getPluginFileNull() {
        File file = JavaPluginAccessors.getPluginFile(null);
        assertNull(file);

    }


}
