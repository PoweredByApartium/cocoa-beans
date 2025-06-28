package net.apartium.cocoabeans.scoreboard.display.fixture;

import net.apartium.cocoabeans.scoreboard.BoardPlayerGroup;
import net.apartium.cocoabeans.scoreboard.display.packet.*;
import net.apartium.cocoabeans.scoreboard.fixture.MockPlayer;
import net.apartium.cocoabeans.scoreboard.packet.Packet;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Set;

public class TestDisplayTeam extends DisplayTeam<MockPlayer> {

    public TestDisplayTeam(String name, BoardPlayerGroup<MockPlayer> group) {
        super(name, group);
    }

    @Override
    public void sendUpdateTeamPacket(Set<MockPlayer> audience, Component displayName, byte friendlyFire, NameTagVisibilityRule nameTagVisibilityRule, CollisionRule collisionRule, ChatFormatting formatting, Component prefix, Component suffix) {
        if (audience.isEmpty())
            return;

        Packet packet = new UpdateTeamPacket(
                displayName,
                friendlyFire,
                nameTagVisibilityRule,
                collisionRule,
                formatting,
                prefix,
                suffix
        );

        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    @Override
    public void sendAddEntitiesPacket(Set<MockPlayer> audience, Collection<String> addEntities) {
        if (audience.isEmpty())
            return;

        Packet packet = new AddEntitiesPacket(addEntities);

        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    @Override
    public void sendRemoveEntitiesPacket(Set<MockPlayer> audience, Collection<String> removeEntities) {
        if (audience.isEmpty())
            return;

        Packet packet = new RemoveEntitiesPacket(removeEntities);

        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    @Override
    public void sendCreateTeamPacket(Set<MockPlayer> audience, Component displayName, byte friendlyFire, NameTagVisibilityRule nameTagVisibilityRule, CollisionRule collisionRule, ChatFormatting formatting, Component prefix, Component suffix, Collection<String> entities) {
        if (audience.isEmpty())
            return;

        Packet packet = new CreateTeamPacket(
                displayName,
                friendlyFire,
                nameTagVisibilityRule,
                collisionRule,
                formatting,
                prefix,
                suffix,
                entities
        );

        for  (MockPlayer player : audience)
            player.sendPacket(packet);
    }

    @Override
    public void sendRemoveTeamPacket(Set<MockPlayer> audience) {
        if (audience.isEmpty())
            return;

        Packet packet = new RemoveTeamPacket();

        for (MockPlayer player : audience)
            player.sendPacket(packet);
    }
}
