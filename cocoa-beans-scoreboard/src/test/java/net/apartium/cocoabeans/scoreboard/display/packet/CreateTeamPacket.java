package net.apartium.cocoabeans.scoreboard.display.packet;

import net.apartium.cocoabeans.packet.Packet;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public record CreateTeamPacket(
        Component displayName,
        byte friendlyFire,
        NameTagVisibilityRule nameTagVisibilityRule,
        CollisionRule collisionRule,
        ChatFormatting formatting,
        Component prefix,
        Component suffix,
        Collection<String> entities
) implements Packet {

}
