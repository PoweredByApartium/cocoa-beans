package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.spigot.visibility.VisibilityGroup;
import net.apartium.cocoabeans.spigot.visibility.VisibilityManager;
import net.apartium.cocoabeans.spigot.visibility.VisibilityPlayerRemoveType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VisibilityManagerTest extends CocoaBeansTestBase {

    private PlayerMock
            ikfir,
            thebotgame,
            ikfirBot,
            voigon,
            cloudflareDNS,
            googleDNS;

    @BeforeEach
    @Override
    public void setup() {
        super.setup();

        ikfir = server.addPlayer("ikfir");
        thebotgame = server.addPlayer("Thebotgame");
        ikfirBot = server.addPlayer("ikfirBot");
        voigon = server.addPlayer("Voigon");
        cloudflareDNS = server.addPlayer("1111DNS");
        googleDNS = server.addPlayer("8888DNS");

    }

    @Test
    void someTestName() {

        plugin.getLogger();

        VisibilityManager visibilityManager = new VisibilityManager(plugin, VisibilityPlayerRemoveType.NEVER);

        VisibilityGroup group = visibilityManager.getOrCreateGroup("test");
        VisibilityGroup group1 = visibilityManager.getOrCreateGroup("test2");

        group.addPlayer(ikfir);
        group.addPlayer(voigon);

        group1.addPlayer(cloudflareDNS);
        group1.addPlayer(googleDNS);

        assertTrue(ikfir.canSee(voigon));
        assertTrue(voigon.canSee(ikfir));

        assertFalse(cloudflareDNS.canSee(ikfir));
        assertFalse(cloudflareDNS.canSee(voigon));

        assertFalse(ikfir.canSee(cloudflareDNS));
        assertFalse(voigon.canSee(cloudflareDNS));

        assertFalse(ikfir.canSee(googleDNS));
        assertFalse(googleDNS.canSee(ikfir));

        assertFalse(thebotgame.canSee(ikfir));
        assertFalse(thebotgame.canSee(voigon));
        assertFalse(thebotgame.canSee(cloudflareDNS));
        assertFalse(thebotgame.canSee(googleDNS));

        assertFalse(ikfir.canSee(thebotgame));
        assertFalse(voigon.canSee(thebotgame));
        assertFalse(cloudflareDNS.canSee(thebotgame));
        assertFalse(googleDNS.canSee(thebotgame));

        assertTrue(thebotgame.canSee(ikfirBot));
        assertTrue(ikfirBot.canSee(thebotgame));

        visibilityManager.registerListener();
        PlayerMock iVoigon = server.addPlayer("iVoigon");

        assertTrue(thebotgame.canSee(iVoigon));
        assertTrue(iVoigon.canSee(thebotgame));


        assertFalse(iVoigon.canSee(ikfir));
        assertFalse(iVoigon.canSee(voigon));
        assertFalse(iVoigon.canSee(cloudflareDNS));
        assertFalse(iVoigon.canSee(googleDNS));

        assertFalse(ikfir.canSee(iVoigon));
        assertFalse(voigon.canSee(iVoigon));
        assertFalse(cloudflareDNS.canSee(iVoigon));
        assertFalse(googleDNS.canSee(iVoigon));

        group1.addPlayer(ikfir);

        assertTrue(cloudflareDNS.canSee(ikfir));
        assertTrue(googleDNS.canSee(ikfir));

        assertTrue(ikfir.canSee(cloudflareDNS));
        assertTrue(ikfir.canSee(googleDNS));

        group.removePlayer(ikfir);

        assertTrue(ikfir.canSee(cloudflareDNS));
        assertTrue(ikfir.canSee(googleDNS));

        assertFalse(ikfir.canSee(voigon));
        assertFalse(voigon.canSee(ikfir));

        group1.removePlayer(ikfir);

        assertFalse(cloudflareDNS.canSee(ikfir));
        assertFalse(googleDNS.canSee(ikfir));

        assertFalse(ikfir.canSee(cloudflareDNS));
        assertFalse(ikfir.canSee(googleDNS));

        assertTrue(ikfir.canSee(thebotgame));
        assertTrue(ikfir.canSee(ikfirBot));

        assertTrue(thebotgame.canSee(ikfir));
        assertTrue(ikfirBot.canSee(ikfir));
    }
}
