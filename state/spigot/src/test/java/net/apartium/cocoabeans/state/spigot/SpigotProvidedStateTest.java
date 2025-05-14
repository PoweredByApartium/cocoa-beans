package net.apartium.cocoabeans.state.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.state.Observable;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpigotProvidedStateTest {

    private SpigotProvidedState providedState;

    private CustomMockServer server;
    private MockPlugin plugin;

    @BeforeEach
    void setup() {
        server = MockBukkit.mock(new CustomMockServer());
        plugin = MockBukkit.createMockPlugin();
        providedState = new SpigotProvidedState(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        server = null;
        plugin = null;
        providedState = null;
    }

    @Test
    void joinAndLeave() {
        assertEquals(0, providedState.getOnlinePlayersObservable().get().size());
        PlayerMock player = server.addPlayer("ikfir");

        assertEquals(1, providedState.getOnlinePlayersObservable().get().size());
        PlayerMock player2 = server.addPlayer("Voigon");

        assertEquals(2, providedState.getOnlinePlayersObservable().get().size());
        player.disconnect();

        assertEquals(1, providedState.getOnlinePlayersObservable().get().size());

        player2.disconnect();
        assertEquals(0, providedState.getOnlinePlayersObservable().get().size());
    }

    @Test
    void currentTick() {
        Observable<Integer> currentTickObservable = providedState.currentTickObservable();
        assertEquals(0, currentTickObservable.get());
        providedState.heartbeat();
        assertEquals(0, currentTickObservable.get());

        server.setCurrentTick(1);
        assertEquals(0, currentTickObservable.get());

        providedState.heartbeat();
        assertEquals(1, currentTickObservable.get());
    }
}
