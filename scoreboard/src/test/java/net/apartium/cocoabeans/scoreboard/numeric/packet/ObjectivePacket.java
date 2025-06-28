package net.apartium.cocoabeans.scoreboard.numeric.packet;

import net.apartium.cocoabeans.scoreboard.ObjectiveMode;
import net.apartium.cocoabeans.scoreboard.ObjectiveRenderType;
import net.apartium.cocoabeans.scoreboard.packet.Packet;
import net.kyori.adventure.text.Component;

public record ObjectivePacket(
        ObjectiveMode mode,
        ObjectiveRenderType renderType,
        Component displayName
) implements Packet {

}
