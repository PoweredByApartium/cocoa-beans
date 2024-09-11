package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.spigot.visibility.TestPlayerVisibilityController;
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

    private VisibilityManager visibilityManager;

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

        visibilityManager = new VisibilityManager(plugin, new TestPlayerVisibilityController());

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

        visibilityManager.registerListener(VisibilityPlayerRemoveType.NEVER);
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

        group.addPlayer(ikfir);

        assertCanSee(ikfir, voigon);

        assertCantSee(ikfir, ikfirBot);
        assertCantSee(ikfir, iVoigon);
        assertCantSee(ikfir, thebotgame);
        assertCantSee(ikfir, googleDNS);
        assertCantSee(ikfir, cloudflareDNS);

        group1 = visibilityManager.getOrCreateGroup("test2");

        group1.addPlayer(cloudflareDNS);
        group1.addPlayer(googleDNS);

        assertCantSee(cloudflareDNS, ikfir);
        assertCantSee(googleDNS, ikfir);

        assertCanSee(cloudflareDNS, googleDNS);

        assertCantSee(cloudflareDNS, ikfirBot);
        assertCantSee(cloudflareDNS, iVoigon);
        assertCantSee(cloudflareDNS, thebotgame);

        assertCantSee(googleDNS, ikfirBot);
        assertCantSee(googleDNS, iVoigon);
        assertCantSee(googleDNS, thebotgame);

        group.addVisibleGroup(group1);

        assertCanSeeOneSide(ikfir, cloudflareDNS);
        assertCanSeeOneSide(ikfir, googleDNS);

        assertCantSeeOneSide(cloudflareDNS, ikfir);
        assertCantSeeOneSide(googleDNS, ikfir);

        assertCanSeeOneSide(voigon, cloudflareDNS);
        assertCanSeeOneSide(voigon, googleDNS);

        assertCantSeeOneSide(cloudflareDNS, voigon);
        assertCantSeeOneSide(googleDNS, voigon);

        group.removeVisibleGroup(group1);

        assertCantSee(ikfir, cloudflareDNS);
        assertCantSee(ikfir, googleDNS);

        assertCantSee(voigon, cloudflareDNS);
        assertCantSee(voigon, googleDNS);

    }

    @Test
    void gameVisibleTest() {
        visibilityManager = new VisibilityManager(plugin, new TestPlayerVisibilityController());

        VisibilityGroup game = visibilityManager.getOrCreateGroup("game");
        VisibilityGroup spectator = visibilityManager.getOrCreateGroup("spectator");

        game.addHiddenGroup(spectator);

        game.addPlayer(ikfir);
        game.addPlayer(voigon);

        assertCanSee(ikfir, voigon);

        spectator.addPlayer(ikfir);

        assertCanSeeOneSide(ikfir, voigon);
        assertCantSeeOneSide(voigon, ikfir);

        spectator.addPlayer(voigon);

        assertCanSee(ikfir, voigon);

        spectator.removePlayer(ikfir);

        assertCanSeeOneSide(voigon, ikfir);
        assertCantSeeOneSide(ikfir, voigon);

        spectator.removePlayer(voigon);

        assertCanSee(ikfir, voigon);
    }

    void assertCanSeeOneSide(PlayerMock player, PlayerMock target) {
        assertTrue(player.canSee(target));
        assertTrue(visibilityManager.canSee(player, target));
    }

    void assertCantSeeOneSide(PlayerMock player, PlayerMock target) {
        assertFalse(player.canSee(target));
        assertFalse(visibilityManager.canSee(player, target));
    }

    void assertCanSee(PlayerMock player, PlayerMock target) {
        assertCanSeeOneSide(player, target);
        assertCanSeeOneSide(target, player);
    }

    void assertCantSee(PlayerMock player, PlayerMock target) {
        assertCantSeeOneSide(player, target);
        assertCantSeeOneSide(target, player);
    }
}
