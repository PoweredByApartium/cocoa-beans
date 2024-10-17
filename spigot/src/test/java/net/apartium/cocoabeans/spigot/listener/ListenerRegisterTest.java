package net.apartium.cocoabeans.spigot.listener;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import net.apartium.cocoabeans.spigot.lazies.ListenerAutoRegistration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

        listenerAutoRegistration.addInjectableObject(myManager);

        assertDoesNotThrow(() -> listenerAutoRegistration.register(getClass().getPackageName(), false, Set.of(FailedListener.class)));

        assertTrue(myManager.hasBeenCreated());

        List<Class<? extends Listener>> list = HandlerList.getHandlerLists().stream()
                .map(HandlerList::getRegisteredListeners)
                .flatMap(Arrays::stream)
                .map(RegisteredListener::getListener)
                .<Class<? extends Listener>>map(Listener::getClass)
                .toList();

        assertFalse(list.contains(FailedListener.class));
        assertTrue(list.contains(AnotherListener.class));
        assertTrue(list.contains(JustListener.class));
        assertTrue(list.contains(MyListener.class));

    }

    @Override
    public void initialize() {

    }
}
