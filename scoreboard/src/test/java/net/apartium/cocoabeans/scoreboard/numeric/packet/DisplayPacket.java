package net.apartium.cocoabeans.scoreboard.numeric.packet;

import net.apartium.cocoabeans.scoreboard.DisplaySlot;
import net.apartium.cocoabeans.packet.Packet;

public record DisplayPacket(DisplaySlot slot, String objectiveId) implements Packet {

}
