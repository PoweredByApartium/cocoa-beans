package net.apartium.cocoabeans.scoreboard.display.packet;

import net.apartium.cocoabeans.packet.Packet;

import java.util.Collection;

public record RemoveEntitiesPacket(
        Collection<String> removeEntities
) implements Packet {
}
