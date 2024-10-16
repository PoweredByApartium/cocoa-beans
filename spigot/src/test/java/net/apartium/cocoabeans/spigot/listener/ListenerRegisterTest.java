package net.apartium.cocoabeans.spigot.listener;

import net.apartium.cocoabeans.spigot.SpigotTestBase;
import net.apartium.cocoabeans.spigot.lazies.ListenerAutoRegistration;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;

public class ListenerRegisterTest extends SpigotTestBase {

    @BeforeEach
    @Override
    public void setup() {
        super.setup();
    }

    @Test
    void testRegister() {
        ListenerAutoRegistration listenerAutoRegistration = new ListenerAutoRegistration(plugin, true);
        MyManager myManager = new MyManager();

        assertFalse(myManager.hasBeenCreated());

        List<String> logs = new ArrayList();

        Handler handler = new Handler() {


            @Override
            public void publish(LogRecord logRecord) {
                if (!logRecord.getMessage().startsWith("Loaded listener "))
                    return;

                logs.add(logRecord.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {

            }
        };

        Bukkit.getLogger().addHandler(handler);

        assertDoesNotThrow(() -> listenerAutoRegistration.register(getClass().getPackageName(), false, List.of(myManager), Set.of(FailedListener.class)));

        Bukkit.getLogger().removeHandler(handler);

        assertTrue(myManager.hasBeenCreated());
        assertEquals(List.of("Loaded listener MyListener!", "Loaded listener AnotherListener!", "Loaded listener JustListener!"), logs);
    }

    @Override
    public void initialize() {

    }
}
