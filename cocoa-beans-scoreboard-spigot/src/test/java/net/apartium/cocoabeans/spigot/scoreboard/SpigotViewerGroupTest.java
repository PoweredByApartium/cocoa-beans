package net.apartium.cocoabeans.spigot.scoreboard;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.state.Observable;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SpigotViewerGroupTest {

    private ServerMock server;
    private PlayerMock alpha;
    private PlayerMock beta;
    private PlayerMock gamma;

    @BeforeEach
    void setup() {
        server = MockBukkit.mock();
        alpha = server.addPlayer("alpha");
        beta  = server.addPlayer("beta");
        gamma = server.addPlayer("gamma");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        server = null;
    }

    @Test
    void addedPlayerAppearsInObservableAndPlayers() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);

        group.add(alpha);
        group.add(beta);

        assertTrue(group.players().contains(alpha));
        assertTrue(group.players().contains(beta));
        assertTrue(group.observePlayers().get().contains(alpha));
        assertTrue(group.observePlayers().get().contains(beta));
    }

    @Test
    void removedPlayerAbsentFromObservableAndPlayers() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);

        group.add(alpha);
        group.add(beta);
        group.remove(alpha);

        assertFalse(group.players().contains(alpha));
        assertFalse(group.observePlayers().get().contains(alpha));
        assertTrue(group.players().contains(beta));
        assertTrue(group.observePlayers().get().contains(beta));
    }

    @Test
    void clearEmptiesGroupCompletely() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);

        group.addAll(List.of(alpha, beta, gamma));
        group.clear();

        assertTrue(group.players().isEmpty());
        assertTrue(group.observePlayers().get().isEmpty());
    }

    @Test
    void removeAllRemovesOnlySpecifiedPlayers() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);

        group.addAll(List.of(alpha, beta, gamma));
        group.removeAll(List.of(alpha, gamma));

        assertFalse(group.players().contains(alpha));
        assertFalse(group.players().contains(gamma));
        assertTrue(group.players().contains(beta));
    }

    @Test
    void observableNotifiesOnAdd() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);

        AtomicInteger evaluations = new AtomicInteger(0);
        Observable<Integer> mapped = group.observePlayers().map(s -> {
            evaluations.incrementAndGet();
            return s.size();
        });

        assertEquals(0, mapped.get()); // prime — count=1
        assertEquals(1, evaluations.get());

        group.add(alpha);
        assertEquals(1, mapped.get()); // re-evaluate after dirty — count=2
        assertEquals(2, evaluations.get());

        group.add(beta);
        assertEquals(2, mapped.get()); // count=3
        assertEquals(3, evaluations.get());
    }

    @Test
    void observableSnapshotUsesWeakCopyAndDoesNotPreventGc() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);

        group.add(alpha);
        group.add(beta);

        // Snapshot obtained before removing the player from the backing set
        Set<Player> snapshot1 = group.observePlayers().get();
        assertEquals(2, snapshot1.size());

        // Remove from the backing set (simulate player disconnect cleanup)
        group.remove(alpha);
        System.gc();

        // New snapshot must not contain the removed player
        Set<Player> snapshot2 = group.observePlayers().get();
        assertFalse(snapshot2.contains(alpha));
        assertTrue(snapshot2.contains(beta));
    }

    @Test
    void playersViewIsUnmodifiable() {
        Set<Player> backing = new HashSet<>();
        SpigotViewerGroup group = new SpigotViewerGroup(backing);
        group.add(alpha);

        Set<Player> players = group.players();
        assertThrows(UnsupportedOperationException.class, () -> players.add(beta));
    }
}
