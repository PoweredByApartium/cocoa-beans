package net.apartium.cocoabeans.scoreboard.packet;

import net.apartium.cocoabeans.scoreboard.ObjectiveMode;
import net.kyori.adventure.text.Component;

public record ObjectivePacket(
        ObjectiveMode mode,
        Component displayName
) implements Packet {

}
