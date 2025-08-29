package net.apartium.cocoabeans.scoreboard.display;

import net.apartium.cocoabeans.scoreboard.display.fixture.TestTeamManager;
import org.junit.jupiter.api.BeforeEach;

class DisplayTeamTestBase {

    TestTeamManager manager;

    @BeforeEach
    void setup() {
        this.manager = new TestTeamManager();
    }

    void heartbeat() {
        this.manager.heartbeat();
    }

}
