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

        assertCanSee(ikfir, voigon);

        assertCantSee(cloudflareDNS, ikfir);
        assertCantSee(cloudflareDNS, voigon);

        assertCantSee(googleDNS, ikfir);
        assertCantSee(googleDNS, voigon);

        assertCantSee(thebotgame, ikfir);
        assertCantSee(thebotgame, voigon);
        assertCantSee(thebotgame, cloudflareDNS);
        assertCantSee(thebotgame, googleDNS);


        assertCanSee(thebotgame, ikfirBot);

        visibilityManager.registerListener();
        PlayerMock iVoigon = server.addPlayer("iVoigon");

        assertCanSee(thebotgame, ikfirBot);
        assertCanSee(thebotgame, iVoigon);

        assertCanSee(ikfirBot, iVoigon);

        assertCantSee(iVoigon, ikfir);
        assertCantSee(iVoigon, voigon);
        assertCantSee(iVoigon, cloudflareDNS);
        assertCantSee(iVoigon, googleDNS);

        group1.addPlayer(ikfir);


        assertCanSee(cloudflareDNS, ikfir);
        assertCanSee(googleDNS, ikfir);

        assertCantSee(iVoigon, ikfir);

        group.removePlayer(ikfir);

        assertCanSee(cloudflareDNS, ikfir);
        assertCanSee(googleDNS, ikfir);

        assertCantSee(voigon, ikfir);

        assertCantSee(ikfir, iVoigon);
        assertCantSee(ikfir, thebotgame);
        assertCantSee(ikfir, ikfirBot);

        group1.removePlayer(ikfir);

        assertCantSee(cloudflareDNS, ikfir);
        assertCantSee(googleDNS, ikfir);

        assertCanSee(ikfir, iVoigon);
        assertCanSee(ikfir, thebotgame);
        assertCanSee(ikfir, ikfirBot);

        assertTrue(visibilityManager.deleteGroup("test2"));

        assertCanSee(cloudflareDNS, googleDNS);

        assertCanSee(cloudflareDNS, ikfir);
        assertCanSee(cloudflareDNS, iVoigon);
        assertCanSee(cloudflareDNS, thebotgame);
        assertCanSee(cloudflareDNS, ikfirBot);

        assertCanSee(googleDNS, ikfir);
        assertCanSee(googleDNS, iVoigon);
        assertCanSee(googleDNS, thebotgame);
        assertCanSee(googleDNS, ikfirBot);

        assertCantSee(cloudflareDNS, voigon);
        assertCantSee(googleDNS, voigon);
    }

    void assertCanSee(PlayerMock player, PlayerMock target) {
        assertTrue(player.canSee(target));
        assertTrue(target.canSee(player));
    }

    void assertCantSee(PlayerMock player, PlayerMock target) {
        assertFalse(player.canSee(target));
        assertFalse(target.canSee(player));
    }
}
