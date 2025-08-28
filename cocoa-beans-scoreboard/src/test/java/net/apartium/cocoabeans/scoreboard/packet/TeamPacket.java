package net.apartium.cocoabeans.scoreboard.packet;

import net.apartium.cocoabeans.packet.Packet;
import net.apartium.cocoabeans.scoreboard.TeamMode;
import net.kyori.adventure.text.Component;

public record TeamPacket(
        int score,
        TeamMode mode,
        Component prefix,
        Component suffix
) implements Packet {

}
