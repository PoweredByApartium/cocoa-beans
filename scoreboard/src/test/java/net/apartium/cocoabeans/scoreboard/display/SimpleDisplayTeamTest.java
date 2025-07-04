package net.apartium.cocoabeans.scoreboard.display;

import net.apartium.cocoabeans.scoreboard.display.fixture.TestDisplayTeam;
import net.apartium.cocoabeans.scoreboard.display.packet.*;
import net.apartium.cocoabeans.fixture.MockPlayer;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleDisplayTeamTest extends DisplayTeamTestBase {

    @Test
    void create() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        assertTrue(team.getEntities().get().isEmpty());
        assertTrue(team.getViewers().players().isEmpty());
    }

    @Test
    void simpleAudience() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void delete() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));
        player.assertNoMorePackets();

        team.delete();
        player.assertNextPacket(new RemoveTeamPacket());
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void addEntity() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        team.addEntity("ikfir");
        player.assertNoMorePackets();

        team.addEntity("Voigon");
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new AddEntitiesPacket(Set.of("ikfir", "Voigon")));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void addAndRemoveEntity() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.addEntity("ikfir");
        team.addEntity("Voigon");

        heartbeat();
        player.assertNextPacket(new AddEntitiesPacket(Set.of("ikfir", "Voigon")));

        team.removeEntity("ikfir");

        heartbeat();
        player.assertNextPacket(new RemoveEntitiesPacket(Set.of("ikfir")));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void addAndFakeRemoveEntity() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.addEntity("ikfir");
        team.addEntity("Voigon");

        heartbeat();
        player.assertNextPacket(new AddEntitiesPacket(Set.of("ikfir", "Voigon")));

        team.removeEntity("ikfir");
        team.addEntity("ikfir");

        heartbeat();
        player.assertNoMorePackets();

    }

    @Test
    void setPrefix() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        MutableObservable<Component> prefix = Observable.mutable(Component.text("prefix "));
        team.setPrefix(prefix);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                Component.text("prefix "),
                null
        ));

        prefix.set(Component.text("Owner "));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                Component.text("Owner "),
                null
        ));

        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

    }

    @Test
    void setSuffix() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        MutableObservable<Component> suffix = Observable.mutable(Component.text(" suffix"));
        team.setSuffix(suffix);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                Component.text(" suffix")
        ));

        suffix.set(Component.text(" You"));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                Component.text(" You")
        ));

        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void setPrefixAndSuffix() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        MutableObservable<Component> prefix = Observable.mutable(Component.text("prefix "));
        MutableObservable<Component> suffix = Observable.mutable(Component.text(" suffix"));
        team.setPrefix(prefix)
                .setSuffix(suffix);

        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                Component.text("prefix "),
                Component.text(" suffix")
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void fakeAudience() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        heartbeat();

        team.getViewers().add(player);
        team.getViewers().remove(player);

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void fakeChange() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.setPrefix(Observable.mutable(Component.text("test")));
        team.setPrefix(Observable.empty());

        heartbeat();
        player.assertNoMorePackets();

        team.setPrefix(Observable.empty());
        heartbeat();
    }

    @Test
    void setDisplayName() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        MutableObservable<Component> displayName = Observable.mutable(Component.text("name"));
        team.setDisplayName(displayName);
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                Component.text("name"),
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null
        ));

        player.assertNoMorePackets();

        displayName.set(Component.text("wow"));
        displayName.set(Component.text("name"));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        displayName.set(Component.text("Testing"));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                Component.text("Testing"),
                (byte) 0x00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void nameTagVisibilityRule() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.setNameTagVisibilityRule(Observable.immutable(NameTagVisibilityRule.ALWAYS));
        team.setNameTagVisibilityRule(Observable.immutable(NameTagVisibilityRule.NEVER));
        team.setNameTagVisibilityRule(Observable.immutable(NameTagVisibilityRule.ALWAYS));

        heartbeat();
        player.assertNoMorePackets();

        team.setNameTagVisibilityRule(Observable.immutable(NameTagVisibilityRule.NEVER));
        heartbeat();

        player.assertNextPacket(new  UpdateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.NEVER,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void collisionRule() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.setCollisionRule(Observable.immutable(CollisionRule.ALWAYS));
        team.setCollisionRule(Observable.immutable(CollisionRule.NEVER));
        team.setCollisionRule(Observable.immutable(CollisionRule.ALWAYS));

        heartbeat();
        player.assertNoMorePackets();

        team.setCollisionRule(Observable.immutable(CollisionRule.NEVER));
        heartbeat();

        player.assertNextPacket(new  UpdateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.NEVER,
                ChatFormatting.RESET,
                null,
                null
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void friendlyFire() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.setFriendlyFire(Observable.immutable((byte) 0b10));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0b10,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();

        team.setFriendlyFire(Observable.immutable((byte) 0b10));
        heartbeat();
        player.assertNoMorePackets();
    }

    @Test
    void formating() {
        TestDisplayTeam team = manager.getTeam("myTeam");
        MockPlayer player = new MockPlayer();

        team.getViewers().add(player);

        heartbeat();
        player.assertNextPacket(new CreateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RESET,
                null,
                null,
                Set.of()
        ));

        team.setFormatting(Observable.immutable(ChatFormatting.RED));
        player.assertNoMorePackets();
        heartbeat();

        player.assertNextPacket(new UpdateTeamPacket(
                null,
                (byte) 0b00,
                NameTagVisibilityRule.ALWAYS,
                CollisionRule.ALWAYS,
                ChatFormatting.RED,
                null,
                null
        ));
        player.assertNoMorePackets();

        heartbeat();
        player.assertNoMorePackets();
    }


}
