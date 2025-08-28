package net.apartium.cocoabeans.tab;

import net.apartium.cocoabeans.fixture.MockPlayer;
import net.apartium.cocoabeans.packet.Packet;
import net.apartium.cocoabeans.scoreboard.ViewerGroup;
import net.apartium.cocoabeans.tab.packet.PlayerListHeaderAndFooter;
import net.kyori.adventure.text.Component;

import java.util.Set;

public class TestTabList extends TabList<MockPlayer> {

    public TestTabList(ViewerGroup<MockPlayer> group) {
        super(group);
    }

    @Override
    protected void sendPlayerListHeaderAndFooter(Set<MockPlayer> viewers, Component header, Component footer) {
        if (viewers.isEmpty())
            return;

        Packet packet = new PlayerListHeaderAndFooter(header, footer);
        for (MockPlayer viewer : viewers) {
            viewer.sendPacket(packet);
        }
    }

}
