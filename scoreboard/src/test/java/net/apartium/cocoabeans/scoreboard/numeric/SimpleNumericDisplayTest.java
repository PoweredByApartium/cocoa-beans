package net.apartium.cocoabeans.scoreboard.numeric;

import net.apartium.cocoabeans.scoreboard.DisplaySlot;
import net.apartium.cocoabeans.scoreboard.ObjectiveMode;
import net.apartium.cocoabeans.scoreboard.ObjectiveRenderType;
import net.apartium.cocoabeans.scoreboard.ScoreboardAction;
import net.apartium.cocoabeans.fixture.MockPlayer;
import net.apartium.cocoabeans.scoreboard.numeric.fixture.TestNumericDisplay;
import net.apartium.cocoabeans.scoreboard.numeric.packet.DisplayPacket;
import net.apartium.cocoabeans.scoreboard.numeric.packet.ObjectivePacket;
import net.apartium.cocoabeans.scoreboard.numeric.packet.ScorePacket;
import net.apartium.cocoabeans.state.CompoundRecords;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimpleNumericDisplayTest extends NumericDisplayBase {

    public static final String ID = "testing";

    @Test
    void create() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());
    }

    @Test
    void addAudience() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        player.assertNoMorePackets();
    }

    @Test
    void addAudienceWithHeartbeat() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        player.assertNoMorePackets();
        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNoMorePackets();

        for (int i = 0; i < 100; i++) {
            heartbeat();
            player.assertNoMorePackets();
        }
    }

    @Test
    void addAndRemoveWithHeartbeat() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        player.assertNoMorePackets();
        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNoMorePackets();

        for (int i = 0; i < 100; i++) {
            heartbeat();
            player.assertNoMorePackets();
        }

        test.getViewers().remove(player);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.REMOVE, ObjectiveRenderType.INTEGER, null));
        player.assertNoMorePackets();
    }

    @Test
    void setDisplayForAudience() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        player.assertNoMorePackets();
        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNoMorePackets();

        for (int i = 0; i < 100; i++) {
            heartbeat();
            player.assertNoMorePackets();
        }

        test.addDisplaySlot(DisplaySlot.BELOW_NAME);
        player.assertNextPacket(new DisplayPacket(DisplaySlot.BELOW_NAME, ID));
        player.assertNoMorePackets();

        test.addDisplaySlot(DisplaySlot.BELOW_NAME);
        player.assertNoMorePackets();

        test.removeDisplaySlot(DisplaySlot.BELOW_NAME);
        player.assertNextPacket(new DisplayPacket(DisplaySlot.BELOW_NAME, null));
        player.assertNoMorePackets();
    }

    @Test
    void setDisplayForNewAudience() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        test.addDisplaySlot(DisplaySlot.BELOW_NAME);

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        player.assertNoMorePackets();
        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNextPacket(new DisplayPacket(DisplaySlot.BELOW_NAME, ID));
        player.assertNoMorePackets();

        for (int i = 0; i < 100; i++) {
            heartbeat();
            player.assertNoMorePackets();
        }
    }

    @Test
    void fakeLeave() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNoMorePackets();

        test.getViewers().remove(player);
        test.getViewers().add(player);

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void setScore() {
        TestNumericDisplay test = manager.getDisplay(ID);
        assertTrue(test.getViewers().players().isEmpty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNoMorePackets();

        MutableObservable<Integer> score = Observable.mutable(1);
        MutableObservable<Component> fixedComponent = Observable.mutable(null);
        MutableObservable<Style> style = Observable.mutable(null);

        final String entityId = "myEntity";

        test.set(entityId, score, fixedComponent, style);

        player.assertNextPacket(new ScorePacket(
                entityId, 1, ScoreboardAction.CREATE_OR_UPDATE, null, null
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        score.set(2);
        score.set(1);
        heartbeat();
        player.assertNoMorePackets();

        score.set(5);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new ScorePacket(
                entityId, 5, ScoreboardAction.CREATE_OR_UPDATE, null, null
        ));

        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void checkSetObservableEmptyGetReplace() throws NoSuchFieldException, IllegalAccessException {
        TestNumericDisplay test = manager.getDisplay(ID);
        final String entityId = "myEntity";

        test.set(entityId, Observable.empty(), Observable.empty(), Observable.empty());
        Observable<CompoundRecords.RecordOf3<Integer, Component, Style>> compound = test.getEntities().get(entityId).getWatchedObservable();

        Field dependsOnField = compound.getClass().getDeclaredField("dependsOn");
        dependsOnField.setAccessible(true);

        Map<Observable<?>, Object> dependsOn = (Map<Observable<?>, Object>) dependsOnField.get(compound);
        Iterator<Observable<?>> iterator = dependsOn.keySet().iterator();
        assertTrue(iterator.hasNext());
        assertNotSame(iterator.next(), Observable.empty());

        assertTrue(iterator.hasNext());
        assertNotSame(iterator.next(), Observable.empty());

        assertTrue(iterator.hasNext());
        assertNotSame(iterator.next(), Observable.empty());

        assertFalse(iterator.hasNext());
    }

    @Test
    void setScoreBeforePlayerJoin() {
        TestNumericDisplay test = manager.getDisplay(ID);
        final String entityId = "myEntity";
        final String secondEntityId = "otherEntity";
        test.set(entityId, Observable.immutable(666), Observable.empty(), Observable.empty());
        test.set(secondEntityId, Observable.immutable(333), Observable.empty(), Observable.empty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNextPacket(new ScorePacket(entityId, 666, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNextPacket(new ScorePacket(secondEntityId, 333, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void setAndRemove() {
        TestNumericDisplay test = manager.getDisplay(ID);
        final String entityId = "myEntity";
        final String secondEntityId = "otherEntity";
        test.set(entityId, Observable.immutable(666), Observable.empty(), Observable.empty());
        test.set(secondEntityId, Observable.immutable(333), Observable.empty(), Observable.empty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNextPacket(new ScorePacket(entityId, 666, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNextPacket(new ScorePacket(secondEntityId, 333, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        test.remove(secondEntityId);
        player.assertNextPacket(new ScorePacket(secondEntityId, 0, ScoreboardAction.REMOVE, null, null));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void setAndDelete() {
        TestNumericDisplay test = manager.getDisplay(ID);
        final String entityId = "myEntity";
        final String secondEntityId = "otherEntity";
        test.set(entityId, Observable.immutable(666), Observable.empty(), Observable.empty());
        test.set(secondEntityId, Observable.immutable(333), Observable.empty(), Observable.empty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);
        assertEquals(Set.of(player), test.getViewers().players());

        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNextPacket(new ScorePacket(entityId, 666, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNextPacket(new ScorePacket(secondEntityId, 333, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        test.delete();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.REMOVE, ObjectiveRenderType.INTEGER, null));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void removeEmpty() {
        TestNumericDisplay test = manager.getDisplay(ID);
        final String entityId = "myEntity";
        final String secondEntityId = "otherEntity";
        test.set(entityId, Observable.immutable(666), Observable.empty(), Observable.empty());
        test.set(secondEntityId, Observable.immutable(333), Observable.empty(), Observable.empty());

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);

        heartbeat();

        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNextPacket(new ScorePacket(entityId, 666, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNextPacket(new ScorePacket(secondEntityId, 333, ScoreboardAction.CREATE_OR_UPDATE, null, null));
        player.assertNoMorePackets();

        test.remove("none");
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void removeDisplayEmpty() {
        TestNumericDisplay test = manager.getDisplay(ID);
        test.addDisplaySlot(DisplaySlot.BELOW_NAME);

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.empty()));
        player.assertNextPacket(new DisplayPacket(DisplaySlot.BELOW_NAME, ID));
        player.assertNoMorePackets();

        test.removeDisplaySlot(DisplaySlot.LIST);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void changeDisplay() {
        TestNumericDisplay test = manager.getDisplay(ID);
        MutableObservable<Component> displayName = Observable.mutable(Component.text("test"));
        test.displayName(displayName);

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.INTEGER, Component.text("test")));
        player.assertNoMorePackets();

        displayName.set(Component.text("test"));

        heartbeat();
        player.assertNoMorePackets();

        displayName.set(Component.text("newTitle"));

        heartbeat();
        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.UPDATE, ObjectiveRenderType.INTEGER, Component.text("newTitle")));
        player.assertNoMorePackets();
    }

    @Test
    void renderType() {
        TestNumericDisplay test = manager.getDisplay(ID);
        test.renderType(ObjectiveRenderType.INTEGER); // Same as it so should change
        test.renderType(ObjectiveRenderType.HEARTS);

        MockPlayer player = new MockPlayer();
        test.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.CREATE, ObjectiveRenderType.HEARTS, Component.empty()));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        test.renderType(ObjectiveRenderType.INTEGER);
        player.assertNextPacket(new ObjectivePacket(ObjectiveMode.UPDATE, ObjectiveRenderType.INTEGER, Component.empty()));

        heartbeat();
        player.assertNoMorePackets();
    }

}
