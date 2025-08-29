package net.apartium.cocoabeans.scoreboard.numeric.packet;

import net.apartium.cocoabeans.packet.Packet;
import net.apartium.cocoabeans.scoreboard.DisplaySlot;

public record DisplayPacket(DisplaySlot slot, String objectiveId) implements Packet {

}
