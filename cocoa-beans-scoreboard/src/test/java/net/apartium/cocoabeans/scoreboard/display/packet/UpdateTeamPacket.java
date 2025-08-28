package net.apartium.cocoabeans.scoreboard.display.packet;

import net.apartium.cocoabeans.packet.Packet;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.kyori.adventure.text.Component;

public record UpdateTeamPacket(
        Component displayName,
        byte friendlyFire,
        NameTagVisibilityRule nameTagVisibilityRule,
        CollisionRule collisionRule,
        ChatFormatting formatting,
        Component prefix,
        Component suffix
) implements Packet {

}
