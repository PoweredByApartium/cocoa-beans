package net.apartium.cocoabeans.spigot.tab;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.spigot.CocoaBeansTestBase;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotViewerGroup;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.WeakHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTabListTest extends CocoaBeansTestBase {

    @Test
    void simpleUse() {
        SpigotViewerGroup group = new SpigotViewerGroup(Collections.newSetFromMap(new WeakHashMap<>()));

        SpigotTabList tabList = new SpigotTabList(group);
        assertTrue(tabList.hasNativeKyori());

        PlayerMock player = addPlayer("ikfir");
        group.add(player);

        assertNull(player.playerListHeader());
        assertNull(player.playerListFooter());

        tabList.set(Component.text("Header"), Component.text("Footer"));
        assertNull(player.playerListFooter());
        assertNull(player.playerListHeader());

        tabList.heartbeat();

        assertEquals(Component.text("Header"), player.playerListHeader());
        assertEquals(Component.text("Footer"), player.playerListFooter());

        group.remove(player);
        tabList.heartbeat();

        assertNull(player.playerListHeader());
        assertNull(player.playerListFooter());
    }

    @Test
    void simpleUseLegacy() {
        SpigotViewerGroup group = new SpigotViewerGroup(Collections.newSetFromMap(new WeakHashMap<>()));

        SpigotTabList tabList = new SpigotTabList(group, false);
        assertFalse(tabList.hasNativeKyori());


        PlayerMock player = server.addPlayer("ikfir");
        group.add(player);

        assertNull(player.playerListHeader());
        assertNull(player.playerListFooter());

        tabList.set(Component.text("Header"), Component.text("Footer"));
        assertNull(player.playerListFooter());
        assertNull(player.playerListHeader());

        tabList.heartbeat();

        assertEquals(Component.text("Header"), player.playerListHeader());
        assertEquals(Component.text("Footer"), player.playerListFooter());

        group.remove(player);
        tabList.heartbeat();

        assertEquals(Component.empty(), player.playerListHeader());
        assertEquals(Component.empty(), player.playerListFooter());
    }

}
