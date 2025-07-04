package net.apartium.cocoabeans.scoreboard.display.packet;

import net.apartium.cocoabeans.packet.Packet;

import java.util.Collection;

public record AddEntitiesPacket(
        Collection<String> addEntities
) implements Packet {

}
