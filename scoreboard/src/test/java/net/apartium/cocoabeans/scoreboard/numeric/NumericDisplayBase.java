package net.apartium.cocoabeans.scoreboard.numeric;

import net.apartium.cocoabeans.scoreboard.numeric.fixture.ScoreboardNumericManager;
import org.junit.jupiter.api.BeforeEach;

class NumericDisplayBase {

    ScoreboardNumericManager manager;

    @BeforeEach
    void setup() {
        manager = new ScoreboardNumericManager();
    }

    void heartbeat() {
        manager.heartbeat();
    }

}
