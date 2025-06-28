package net.apartium.cocoabeans.scoreboard.fixture;

import net.apartium.cocoabeans.scoreboard.packet.Packet;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class MockPlayer {

    private final Queue<Packet> packets = new LinkedList<>();

    public void sendPacket(Packet packet) {
        packets.add(packet);
    }

    public void assertNextPacket(Packet expected) {
        assertFalse(packets.isEmpty(), "Packet is empty while it should have another packet!");
        Packet actual = packets.poll();
        assertEquals(expected.getClass(), actual.getClass(), "Not the same packet type!");
        assertEquals(expected, actual);
    }

    public void assertNoMorePackets() {
        assertTrue(packets.isEmpty(), "Packets should be empty");
    }

}
