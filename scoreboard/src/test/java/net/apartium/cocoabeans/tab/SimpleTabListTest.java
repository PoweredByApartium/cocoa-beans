package net.apartium.cocoabeans.tab;

import net.apartium.cocoabeans.fixture.MockPlayer;
import net.apartium.cocoabeans.fixture.TestViewerGroup;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.tab.packet.PlayerListHeaderAndFooter;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTabListTest {

    @Test
    void simpleUse() {
        TestViewerGroup group = new TestViewerGroup(new HashSet<>());

        TestTabList tabList = new TestTabList(group);

        assertEquals(group, tabList.getGroup());

        tabList.set(Component.text("Header"), Component.text("Footer"));

        MockPlayer player = new MockPlayer();
        player.assertNoMorePackets();

        group.add(player);
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                Component.text("Header"),
                Component.text("Footer")
        ));
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNoMorePackets();

        tabList.delete();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                null,
                null
        ));
        player.assertNoMorePackets();
    }

    @Test
    void fakeRemoveViewer() {
        TestViewerGroup group = new TestViewerGroup(new HashSet<>());

        TestTabList tabList = new TestTabList(group);

        assertEquals(group, tabList.getGroup());

        tabList.set(Component.text("Header"), Component.text("Footer"));

        MockPlayer player = new MockPlayer();
        player.assertNoMorePackets();

        group.add(player);
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                Component.text("Header"),
                Component.text("Footer")
        ));
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNoMorePackets();

        group.remove(player);
        group.add(player);
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNoMorePackets();

        tabList.delete();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                null,
                null
        ));
        player.assertNoMorePackets();
    }




    @Test
    void dynamicObservable() {
        TestViewerGroup group = new TestViewerGroup(new HashSet<>());

        TestTabList tabList = new TestTabList(group);

        assertEquals(group, tabList.getGroup());

        MutableObservable<Component> footer = Observable.mutable(Component.text("Footer"));
        tabList.set(Observable.immutable(Component.text("Header")), footer);

        MockPlayer player = new MockPlayer();
        player.assertNoMorePackets();

        group.add(player);
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                Component.text("Header"),
                Component.text("Footer")
        ));
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNoMorePackets();

        footer.set(Component.text("Footer"));
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNoMorePackets();

        footer.set(Component.text("My Footer"));
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                Component.text("Header"),
                Component.text("My Footer")
        ));
        player.assertNoMorePackets();

        tabList.heartbeat();
        player.assertNoMorePackets();

        tabList.delete();
        player.assertNextPacket(new PlayerListHeaderAndFooter(
                null,
                null
        ));
        player.assertNoMorePackets();
    }



}
